package soeclient

import akka.actor. { Actor, ActorPath }

object PrimesList {

  case object Get
  case class Insert(number : Integer)

}

class PrimesList extends Actor {
  import PrimesList._

  var list = scala.collection.mutable.ListBuffer.empty[Int]

  def receive = {
    case Insert(number) =>
      if(!list.contains(number)) {
        list.append(number)
        list.sorted
      }
    case Get =>
      sender ! list.toList
  }

}
