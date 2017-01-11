package soeendpoint

import akka.actor.{ ActorRef, ActorSystem, Props }

import com.typesafe.config.ConfigFactory

import Endpoint._

object SoeEndpoint extends App {

  lazy val appConfig = ConfigFactory.load()
  lazy val name = appConfig.getString("endpoint-remote.soe.endpointName")

  override
  def main(args: Array[String]): Unit = {
    // Create the 'soeEndpoint' actor system
    val sys = ActorSystem("soeEndpoint")
    val endpoint = sys.actorOf(Props[Endpoint], name)
  }

}
