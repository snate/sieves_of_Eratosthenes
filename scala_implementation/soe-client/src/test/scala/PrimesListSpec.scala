package soeclient

import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, Matchers }
import akka.actor.{ Actor, Props, ActorSystem }
import akka.testkit.{ ImplicitSender, TestKit, TestActorRef }
import scala.concurrent.duration._

import PrimesList._

class PrimesListSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("PrimesListSpec"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "A PrimesList" should "be initially empty" in {
    val list = TestActorRef(Props[PrimesList])
    list ! Get
    expectMsgType[List[Int]] should be(List())
  }

  it should "be able to insert a new number" in {
    val list = TestActorRef(Props[PrimesList])
    list ! Insert(2)
    list.underlyingActor.asInstanceOf[PrimesList].list should be(List(2))
  }

  it should "be able to provide the numbers' list after inserting one" in {
    val list = TestActorRef(Props[PrimesList])
    list ! Insert(2)
    list ! Get
    expectMsgType[List[Int]] should be(List(2))
  }

  it should "not add the same number twice" in {
    val list = TestActorRef(Props[PrimesList])
    list ! Insert(2)
    list ! Get
    expectMsgType[List[Int]] should be(List(2))
    list ! Insert(2)
    list ! Get
    expectMsgType[List[Int]] should be(List(2))
  }

  it should "be able to provide numbers' list after inserting more than one" in {
    val list = TestActorRef(Props[PrimesList])
    list ! Insert(2)
    list ! Get
    expectMsgType[List[Int]] should be(List(2))
    list ! Insert(5)
    list ! Get
    expectMsgType[List[Int]] should be(List(2,5))
  }
}
