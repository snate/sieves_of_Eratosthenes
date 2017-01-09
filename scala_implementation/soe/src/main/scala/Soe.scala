package soe

import akka.actor.{ Actor, ActorRef, ActorSystem, Props, OneForOneStrategy }
import akka.actor.SupervisorStrategy._

import scala.concurrent.duration._

import Receiver.{ NextNumber }

import types.Messages._

object Soe {
  case object Something
}

class Soe extends Actor {
  import Soe._

  override
  val supervisorStrategy = OneForOneStrategy(5, 1 minute) {
    case _ => Restart
  }

  override
  def preStart() = {
    // Instantiate SieveManager and Receiver
    val manager  = context.actorOf(Props[SieveManager],"SieveManager")
    val receiver = context.actorOf(Props(classOf[Receiver], manager),"Receiver")
  }

  def receive = {

    case CheckPrimality(num : Integer) =>
      val receiver = context.actorSelection("Receiver")
      sender ! "Done"
      receiver ! NextNumber(0,num)

    case CheckPrimalityWithId(num : Integer, id : Integer) =>
      val receiver = context.actorSelection("Receiver")
      sender ! "Done"
      receiver ! NextNumber(id,num)

    case _ =>
  }

}
