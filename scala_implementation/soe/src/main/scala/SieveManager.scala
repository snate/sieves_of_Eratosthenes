package soe

import akka.actor.{ Actor, ActorRef, ActorSystem, Props, OneForOneStrategy }
import akka.actor.SupervisorStrategy._

import scala.concurrent.duration._

object SieveManager {
  case object Created
  case object AlreadyPresent
  case class SieveWithId(id : Integer, number : Integer)

  val SievePrefix = "Sieve"
}

class SieveManager extends Actor {
  import SieveManager._

  override
  val supervisorStrategy = OneForOneStrategy(5, 1 minute) {
    case _ => Restart
  }

  val refs = collection.mutable.Map[Integer, ActorRef]()

  def receive = {
    case SieveWithId(id, number) =>
      val props = Props(classOf[Sieve], number, id)
      val name  = SievePrefix + id.toString
      val sieve = refs.getOrElseUpdate (id, context.actorOf(props,name))
      sender ! sieve
  }

}
