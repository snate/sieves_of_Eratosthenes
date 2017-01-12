package types

object Messages  {
  // backend interface
  case class CheckPrimality(num : Integer)
  // endpoint interface
  case object Registered
  case class AskFor(number : Integer)
  case class AnswerFor(number : Integer, isPrime : Boolean)
}
