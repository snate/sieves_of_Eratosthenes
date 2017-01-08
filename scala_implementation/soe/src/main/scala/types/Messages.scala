package types

object Messages  {
  // client interface
  case class CheckPrimality(num : Integer)
  // endpoint interface
  case class AnswerFor(number : Integer, isPrime : Boolean)
  // internal interface
  case class CheckPrimalityWithId(number : Integer, id : Integer)
}
