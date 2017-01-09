package types

object Messages {
  // client interface
  case object Registered
  case class AskFor(number : Integer)
  // backend & client interface
  case class AnswerFor(number : Integer, isPrime : Boolean)
}
