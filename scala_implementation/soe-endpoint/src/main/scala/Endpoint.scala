package soeendpoint

import akka.actor.{ Actor, ActorSelection }
import akka.pattern.{ ask }
import akka.util.{ Timeout }

import types.Messages._

object Endpoint {
}

class Endpoint extends Actor {
  import Endpoint._

  val requests = collection.mutable.Map[ Integer, List[ActorSelection] ]()

  def receive = {
    case AskFor(number) =>
      val askerPath = sender().path.parent.parent./("user")./("Computer")
      val askerSel = context.actorSelection(askerPath)
      requests get number match {
        case Some(list) =>
          if(!list.contains(askerSel))
            requests put (number, askerSel :: list)
        case None =>
          requests put (number, List(askerSel))
      }
      sender ! Registered
    case AnswerFor(number, isPrime) =>
      if(requests isDefinedAt number){
        val askers = requests(number)
        askers.map (ref => ref ! AnswerFor(number,isPrime))
      }
      requests remove number
  }

}
