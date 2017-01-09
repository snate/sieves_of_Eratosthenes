package soeclient

import akka.actor.{ ActorRef, ActorSystem, Props }

import com.typesafe.config. { ConfigFactory, Config }

import Computer.{ CheckPrimalityUpTo }

object SoeClient extends App {

  lazy val appConfig = ConfigFactory.load()
  lazy val cliHost =
        appConfig.getString("client-remote.cli-host")
  lazy val cliPort =
        appConfig.getInt("client-remote.cli-port")

  override
  def main(args: Array[String]): Unit = {
    if(args.length < 1){
      println("Usage: sbt \"run-main soeclient.SoeClient number port\"")
      println("Default port: 2551")
      return
    }
    val up_to = args(0).toInt
    var port = cliPort
    if(args.length >= 2)
      port = args(1).toInt
    val configs = configsForClientWithPort(port)
    val sys  = ActorSystem("SoeClient", configs)
    val list = sys.actorOf(Props[PrimesList], "Result")
    val cli  = sys.actorOf(Props(classOf[Computer], list), "Computer")
    cli ! CheckPrimalityUpTo(up_to)
  }

  private def configsForClientWithPort(port : Integer) : Config = {
    val configStr = s"""
      |akka.remote.netty.tcp.hostname = $cliHost
      |akka.remote.netty.tcp.port = $port
      """.stripMargin
    ConfigFactory.parseString(configStr).withFallback(appConfig)
  }

}