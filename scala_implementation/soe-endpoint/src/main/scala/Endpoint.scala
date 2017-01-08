package soe.endpoint

import akka.actor.{ Actor, ActorRef }
import akka.pattern.{ ask }
import akka.util.{ Timeout }

import types.Messages._

object Endpoint {
}

class Endpoint extends Actor {
  import Endpoint._

  val requests = collection.mutable.Map[ Integer, List[ActorRef] ]()

  def receive = {
    case AskFor(number) =>
      val asker = sender()
      println(asker)
      requests get number match {
        case Some(list) => 
          requests put (number, asker :: list)
        case None =>
          requests put (number, List(asker))
      }
    case AnswerFor(number, isPrime) =>
      if(requests isDefinedAt number){
        val askers = requests(number)
        askers.map (ref => ref ! AnswerFor(number,isPrime))
      }
      requests remove number
  }

}
