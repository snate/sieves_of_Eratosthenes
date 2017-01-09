package soe

import akka.actor.{ Actor, ActorRef }
import akka.pattern.{ ask }
import akka.util.{ Timeout }

import scala.concurrent.Await
import scala.concurrent.duration._

import Sieve.{ IsPrime }
import SieveManager.{ SieveWithId }

object Receiver {
  case class NextNumber(id : Integer, number : Integer)
  case object Done
  implicit val timeout = Timeout(5.second)
}

class Receiver(manager : ActorRef) extends Actor {
  import Receiver._

  def receive = {
    case NextNumber(id, number) =>
      val nextId = id + 1
      val sieveReq = manager ? SieveWithId(nextId, number)
      val sieve = Await.result(sieveReq, timeout.duration)
                       .asInstanceOf[ActorRef]
      sieve ! IsPrime(number)
  }

}
