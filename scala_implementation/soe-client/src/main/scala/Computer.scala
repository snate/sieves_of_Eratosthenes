package soeclient

import akka.actor. { Actor, ActorPath, ActorRef }
import akka.pattern.{ ask }
import akka.util.{ Timeout }

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import ExecutionContext.Implicits.global

import com.typesafe.config.ConfigFactory

import types.Messages._
import PrimesList. { Get, Insert }

object Computer {

  lazy val appConfig  = ConfigFactory.load()

  case class CheckPrimalityUpTo(number : Integer)
  case class AskToBackendFor(number : Integer)

  implicit val timeout = Timeout(5.second)

}

class Computer(resultList : ActorRef, pathProv : PathProvider) extends Actor {
  import Computer._

  var count : Integer = 0
  var start_time : Long = 0

  def receive = {
    case CheckPrimalityUpTo(limit) =>
      start_time = System.currentTimeMillis
      for( n <- 2 to limit )
        registerForAnswer(n)
      count = limit - 1
    case AskToBackendFor(n) =>
      askToBackend(n)
    case AnswerFor(number, isPrime) =>
      count = count - 1
      if(isPrime)
        resultList ! Insert(number)
      if(count == 0)
        printResult
  }

  private def registerForAnswer(num : Integer) : Unit = {
    val endPath : String = pathProv.pathToEndpoint
    val endpoint = context.actorSelection(endPath)
    val ackRequest = endpoint ? AskFor(num)
    ackRequest onComplete {
      case Success(_) => self ! AskToBackendFor(num)
      case Failure(_) => registerForAnswer(num)
    }
  }

  private def askToBackend(num : Integer) = {
    val soePath : String = pathProv.pathToBackend
    val soe = context.actorSelection(soePath)
    soe ! CheckPrimality(num)
  }

  private def printResult = {
    val endTime = System.currentTimeMillis
    val elapsedSecs = (endTime - start_time) / 1000.0
    println(s"It took $elapsedSecs seconds.")
    val resultReq = resultList ? Get
    val result = Await.result(resultReq, timeout.duration)
                     .asInstanceOf[List[Integer]]
    println(result.sorted)
  }

}
