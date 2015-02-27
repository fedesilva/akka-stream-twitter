package sample.stream

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Source
import twitter4j.{ Twitter, TwitterFactory, User }
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder
import scala.util.Try
import scala.collection._
import scala.concurrent._
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ConcurrentLinkedQueue
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object TwitterClient {
  // Fill these in with your own credentials
  val config = ConfigFactory.load()
  val appKey: String = config.getString("appKey")
  val appSecret: String = config.getString("appSecret")
  val accessToken: String = config.getString("accessToken")
  val accessTokenSecret: String = config.getString("accessTokenSecret")

  def apply(): Twitter = {
    val factory = new TwitterFactory(new ConfigurationBuilder().build())
    val t = factory.getInstance()
    t.setOAuthConsumer(appKey, appSecret)
    t.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret))
    t
  }
}

object TwitterHelpers {
  // Lookup user profiles in batches of 100
  def lookupUsers(ids: List[Long]): List[User] = {
    val client = TwitterClient()
    val res = client.lookupUsers(ids.toArray)
    res.asScala.toList
  }

  // Fetch the IDs of a user's followers in batches of 5000
  def getFollowers(userId: Long): Try[Set[Long]] = {
    Try({
      val followerIds = mutable.Set[Long]()
      var cursor = -1L
      do {
        val client = TwitterClient()
        val res = client.friendsFollowers().getFollowersIDs(userId, cursor, 5000)
        res.getIDs.toList.foreach(x => followerIds.add(x))
        if (res.hasNext) {
          cursor = res.getNextCursor
        } else {
          cursor = -1 // Exit the loop
        }
      } while (cursor > 0)
      followerIds.toSet
    })
  }
}

object Main extends App {

  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("centaur")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)
  implicit val materializer = ActorFlowMaterializer()(system)

  val startTime = System.nanoTime()
  val userId = 410939902L // @headinthebox ~12K followers
  val output = new ConcurrentLinkedQueue[String]()
  println(s"Fetching follower profiles for $userId")

  val future = Source(() => TwitterHelpers.getFollowers(userId).get.toIterable.iterator)
    .grouped(100)
    .mapAsyncUnordered { x => Future[List[User]](TwitterHelpers.lookupUsers(x.toList)) }
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
