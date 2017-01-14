package soeclient

import akka.actor. { Actor, ActorPath, ActorRef }

import com.typesafe.config.ConfigFactory

object PathProvider {

  lazy val appConfig  = ConfigFactory.load()

  lazy val soeBackend = appConfig.getString("client-remote.soe-backend")
  lazy val soeHost    = appConfig.getString("client-remote.soe-host")
  lazy val soePort    = appConfig.getInt("client-remote.soe-port")
  lazy val soeSys     = appConfig.getString("client-remote.soe-sys")

  lazy val soeEndp    = appConfig.getString("client-remote.end-name")
  lazy val endpHost   = appConfig.getString("client-remote.end-host")
  lazy val endpPort   = appConfig.getInt("client-remote.end-port")
  lazy val endpSys    = appConfig.getString("client-remote.end-sys")

}

class PathProvider {
  import PathProvider._

  def pathToEndpoint : String = address(endpSys, endpHost, endpPort, soeEndp)

  def pathToBackend : String = address(soeSys, soeHost, soePort, soeBackend)

  private def address(sys : String, host : String, port : Integer,
                      name : String) : String = {
    s"akka.tcp://$sys@$host:$port/user/$name"
  }

}
