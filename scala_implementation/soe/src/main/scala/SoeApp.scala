package soe

import akka.actor.{ ActorRef, ActorSystem, Props }

import com.typesafe.config.ConfigFactory

import SieveManager.{ SieveWithId }
import Receiver.{ NextNumber }

object SoeApp extends App {

  lazy val appConfig = ConfigFactory.load()
  lazy val nodesNumber =
        appConfig.getInt("backend-remote.soe.numberOfNodes")

  lazy val beName =
        appConfig.getString("backend-remote.soe.backendName")
  lazy val beHost =
        appConfig.getString("backend-remote.soe.backendHost")
  lazy val beSys =
        appConfig.getString("backend-remote.soe.backendSystem")
  lazy val startPort =
        appConfig.getInt("backend-remote.soe.startingSievePort")

  override
  def main(args: Array[String]): Unit = {
    for(i <- 0 to nodesNumber-1) {
      val port = startPort + i;
      val configStr = s"""
        |akka.remote.netty.tcp.hostname = $beHost
        |akka.remote.netty.tcp.port = $port
        """.stripMargin
      val configs = ConfigFactory.parseString(configStr).withFallback(appConfig)
      // Create one 'soe' actor systems for each node
      val sys = ActorSystem(s"$beSys$i", configs)
      sys.actorOf(Props[Soe], beName)
    }
    println(s"System started")
  }

}
