package sample.stream

import java.util.concurrent.{ConcurrentLinkedQueue, ExecutorService, Executors}

import akka.actor._
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Source
import twitter4j._

import scala.concurrent._

object Main extends App {

  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("centaur")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)
  implicit val materializer = ActorFlowMaterializer()(system)

  val startTime = System.nanoTime()
  val userId = 410939902L
  // @headinthebox ~12K followers
  val output = new ConcurrentLinkedQueue[String]()
  println(s"Fetching follower profiles for $userId")

  val future = Source(() => TwitterHelpers.getFollowers(userId).get.toIterable.iterator)
    .grouped(100)
    .mapAsyncUnordered { x => Future[List[User]](TwitterHelpers.lookupUsers(x.toList))}
    .mapConcat(identity)
    .runForeach(x => output.offer(x.getScreenName))

  future.onFailure({
    case t =>
      println("Failed.")
      t.printStackTrace()
  })

  future.onComplete({
    case _ =>
      println(s"Fetched ${output.size()} profiles")
      println(s"Time taken: ${(System.nanoTime() - startTime) / 1000000000.00}s")
      system.shutdown()
      Runtime.getRuntime.exit(0)
  })

}
