package soe

import akka.actor.{ Actor, ActorPath }

import com.typesafe.config.ConfigFactory

import types.Messages._

object Sieve {

  case class IsPrime(number : Integer)

  lazy val appConfig = ConfigFactory.load()
  lazy val nodesNumber =
        appConfig.getInt("backend-remote.soe.numberOfNodes")

  lazy val beName =
        appConfig.getString("backend-remote.soe.backendName")
  lazy val beHost =
        appConfig.getString("backend-remote.soe.backendHost")
  lazy val baseBePort =
        appConfig.getInt("backend-remote.soe.startingSievePort")
  lazy val beSys =
        appConfig.getString("backend-remote.soe.backendSystem")

  lazy val endpName =
        appConfig.getString("backend-remote.soe.endpointName")
  lazy val endpHost =
        appConfig.getString("backend-remote.soe.endpointHost")
  lazy val endpPort =
        appConfig.getInt("backend-remote.soe.endpointPort")
  lazy val endpSys  =
        appConfig.getString("backend-remote.soe.endpointSystem")

}

class Sieve(prime : Integer, id : Integer) extends Actor {
  import Sieve._

  def receive = {
    case IsPrime(number) =>
      if(number ==  prime) {
        sendToEndpoint(AnswerFor(number, true))
        println(s"$number PRIME")
      } else if(number % prime != 0) {
        forwardToNextSieve(CheckPrimalityWithId(number,id))
        println(s"$number POSSIBLE PRIME")
      } else {
        sendToEndpoint(AnswerFor(number, false))
        println(s"$number NOT PRIME")
      }
  }

  private def sendToEndpoint(msg : Any) = {
    val endpPath = address(endpSys, endpHost, endpPort, endpName)
    val endpointRef = context.actorSelection(endpPath)
    endpointRef ! msg
  }

  private def forwardToNextSieve(msg : Any) = {
    val nextPort = baseBePort + nextId
    val nextSys  = beSys + nextId.toString
    val nextSieveSysPath = address(nextSys, beHost, nextPort, beName)
    val nextSieveSys = context.actorSelection(nextSieveSysPath)
    nextSieveSys ! msg
    
  }

  private def address(sys : String, host : String, port : Integer,
                      name : String) : ActorPath = {
    val path = s"akka.tcp://$sys@$host:$port/user/$name"
    return ActorPath.fromString(path)
  }

  private def nodeId : Integer = {
    val systemName = context.parent.path.toString().split('/') (2)
    systemName.slice(3,systemName.length).toInt
  }

  private def nextId : Integer = {
    (nodeId + 1) % nodesNumber
  }

}
