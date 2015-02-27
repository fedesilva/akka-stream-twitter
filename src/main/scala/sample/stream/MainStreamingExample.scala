package sample.stream

package sample.stream

import akka.actor._
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Source
import twitter4j._
import scala.concurrent._
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ConcurrentLinkedQueue

object MainStreamingExample extends App {

  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("centaur")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)
  implicit val materializer = ActorFlowMaterializer()(system)

  // create a TwitterStreamClient, that pubbish on the event bus
  val twitterStream = new TwitterStreamClient(system).init

  // create a Source, with an actor that listen items from the event bus
  val tweets: Source[Status] = Source(Props[StatusPublisherActor])

  // do the magic
  tweets.runForeach { s => println(s.getText) }

}
