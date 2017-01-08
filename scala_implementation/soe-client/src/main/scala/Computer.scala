package soeclient

import akka.actor. { Actor, ActorPath }

import com.typesafe.config.ConfigFactory

import types.Messages._

object Computer {

  lazy val appConfig  = ConfigFactory.load()

  lazy val soeBackend = appConfig.getString("client-remote.soe-backend")
  lazy val soeHost    = appConfig.getString("client-remote.soe-host")
  lazy val soePort    = appConfig.getInt("client-remote.soe-port")
  lazy val soeSys     = appConfig.getString("client-remote.soe-sys")

  lazy val soeEndp    = appConfig.getString("client-remote.end-name")
  lazy val endpHost   = appConfig.getString("client-remote.end-host")
  lazy val endpPort   = appConfig.getInt("client-remote.end-port")
  lazy val endpSys    = appConfig.getString("client-remote.end-sys")

  case class CheckPrimalityUpTo(number : Integer)

}

class Computer extends Actor {
  import Computer._

  var count : Integer = 0

  def receive = {
    case CheckPrimalityUpTo(limit) =>
      for( n <- 2 to limit) {
        registerForAnswer(n)
        askToBackend(n)
      }
    case AnswerFor(number, isPrime) =>
      isPrime match {
        case true  => println(s"Oh, so $number is indeed prime")
        case false => println(s"Well, $number is not prime")
      }
  }

  private def registerForAnswer(num : Integer) = {
    val endPath : ActorPath = address(endpSys, endpHost, endpPort, soeEndp)
    val endpoint = context.actorSelection(endPath)
    endpoint ! AskFor(num)
  }

  private def askToBackend(num : Integer) = {
    val soePath : ActorPath = address(soeSys, soeHost, soePort, soeBackend)
    println(soePath)
    val soe = context.actorSelection(soePath)
    soe ! CheckPrimality(num)
  }

  private def address(sys : String, host : String, port : Integer,
                      name : String) : ActorPath = {
    val path = s"akka.tcp://$sys@$host:$port/user/$name"
    return ActorPath.fromString(path)
  }

}
