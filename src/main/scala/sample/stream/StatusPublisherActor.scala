package sample.stream

import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl._
import akka.actor.{ Props, ActorSystem }
import akka.stream.FlowMaterializer
import twitter4j._

/**
 * A simple ActorPublisher[Status], that receives Status from the event bus
 * and forwards to the Source
 * 
 * TODO: This is a tiny example, so edge cases are not covered (yet).
 */
class StatusPublisherActor extends ActorPublisher[Status] {

  val sub = context.system.eventStream.subscribe(self, classOf[Status])

  override def receive: Receive = {
    case s: Status => { if (isActive && totalDemand > 0) onNext(s) }
    case _         =>
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self)
  }

}