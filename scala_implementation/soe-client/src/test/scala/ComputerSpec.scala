package soeclient

import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, Matchers }
import akka.actor.{ Actor, ActorPath, Props, ActorSystem }
import akka.testkit.{ ImplicitSender, TestKit, TestActorRef }
import scala.concurrent.duration._

import types.Messages._
import Computer._

class ComputerSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  /*
  Auxiliary class for primes' list mock
  */
  class TestPrimesList extends PrimesList {
    import PrimesList._

    var insCalled : Boolean = false
    var getCalled : Boolean = false

    override def receive = {
      case Insert(number) =>
        insCalled = true
      case Get =>
        getCalled = true
        sender ! List()
    }
  }

  /*
  Auxiliary class for path provider mock
  */
  class TestPathProvider extends PathProvider {
    import PathProvider._

    var backend = ""
    var endpoint = ""

    override
    def pathToBackend  : String = backend
    override
    def pathToEndpoint : String = endpoint
  }

  /*
  Auxiliary class for endpoint mock
  */
  class TestEndpoint extends Actor {

    var askedFor = scala.collection.mutable.ListBuffer.empty[Int]

    def receive = {
      case AskFor(num) =>
        askedFor.append(num)
        sender ! Registered
    }
  }

  /*
  Auxiliary class for backend mock
  */
  class TestBackend extends Actor {

    var reqList = scala.collection.mutable.ListBuffer.empty[Int]

    def getReqList = reqList.toList

    def receive = {
      case CheckPrimality(num) =>
        reqList.append(num)
    }
  }

  def this() = this(ActorSystem("ComputerSpec"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "A Computer" should "call insert on PrimesList when receiving a prime" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    computer.underlyingActor.asInstanceOf[Computer].count = 5
    computer ! AnswerFor(2, true)
    list.underlyingActor.asInstanceOf[TestPrimesList].insCalled should be(true)
  }

  it should "not call insert on PrimesList when receveing a non-prime" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    computer.underlyingActor.asInstanceOf[Computer].count = 5
    computer ! AnswerFor(2, false)
    list.underlyingActor.asInstanceOf[TestPrimesList].insCalled should be(false)
  }

  it should "not call insert on PrimesList if it receives the last number/1" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    computer.underlyingActor.asInstanceOf[Computer].count = 1
    computer ! AnswerFor(2, true)
    list.underlyingActor.asInstanceOf[TestPrimesList].insCalled should be(true)
    list.underlyingActor.asInstanceOf[TestPrimesList].getCalled should be(true)
  }

  it should "not call insert on PrimesList if it receives the last number/2" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    computer.underlyingActor.asInstanceOf[Computer].count = 1
    computer ! AnswerFor(2, false)
    list.underlyingActor.asInstanceOf[TestPrimesList].insCalled should be(false)
    list.underlyingActor.asInstanceOf[TestPrimesList].getCalled should be(true)
  }

  it should "be able to ask to the backend for the primality of a number" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    val backend = TestActorRef(Props(new TestBackend()), "backend1")
    pp.backend = "../backend1"
    computer ! AskToBackendFor(3)
    backend.underlyingActor.asInstanceOf[TestBackend].reqList should be(List(3))
  }

  it should "be able to ask to the backend for the primality of two numbers" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    val backend = TestActorRef(Props(new TestBackend()), "backend2")
    pp.backend = "../backend2"
    computer ! AskToBackendFor(3)
    computer ! AskToBackendFor(4)
    backend.underlyingActor.asInstanceOf[TestBackend].reqList should be(List(3,4))
  }

  it should "be able to register itself to the endpoint for 2" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    val endpoint = TestActorRef(Props(new TestEndpoint()), "endpoint1")
    pp.endpoint = "../endpoint1"
    val backend = TestActorRef(Props(new TestBackend()), "backend3")
    pp.backend = "../backend3"
    computer ! CheckPrimalityUpTo(2)
    endpoint.underlyingActor.asInstanceOf[TestEndpoint].askedFor should be(List(2))
    expectNoMsg(50 milliseconds)
    backend.underlyingActor.asInstanceOf[TestBackend].getReqList should be(List(2))
  }

  it should "be able to register itself to the endpoint for 2,3" in {
    val list = TestActorRef(Props(new TestPrimesList))
    val pp = new TestPathProvider()
    val computer = TestActorRef(Props(new Computer(list, pp)))
    val endpoint = TestActorRef(Props(new TestEndpoint()), "endpoint2")
    pp.endpoint = "../endpoint2"
    val backend = TestActorRef(Props(new TestBackend()), "backend4")
    pp.backend = "../backend4"
    computer ! CheckPrimalityUpTo(3)
    endpoint.underlyingActor.asInstanceOf[TestEndpoint].askedFor should be(List(2,3))
    expectNoMsg(50 milliseconds)
    backend.underlyingActor.asInstanceOf[TestBackend].reqList.toList should be(List(2,3))
  }

}
