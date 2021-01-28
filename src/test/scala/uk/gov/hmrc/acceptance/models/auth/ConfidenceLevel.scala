package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

sealed abstract class ConfidenceLevel(val level: Int)

object ConfidenceLevel {

  case object L500 extends ConfidenceLevel(500)

  case object L300 extends ConfidenceLevel(300)

  case object L200 extends ConfidenceLevel(200)

  case object L100 extends ConfidenceLevel(100)

  case object L50 extends ConfidenceLevel(50)

  case object L0 extends ConfidenceLevel(0)

  def fromInt(level: Int): ConfidenceLevel = level match {
    case 500 => L500
    case 300 => L300
    case 200 => L200
    case 100 => L100
    case 50 => L50
    case 0 => L0
    case _ => throw new NoSuchElementException(s"Illegal confidence level: $level")
  }

  implicit val format: Format[ConfidenceLevel] = {
    val reads = Reads[ConfidenceLevel] { json =>
      Try {
        fromInt(json.as[Int])
      } match {
        case Success(level) => JsSuccess(level)
        case Failure(ex) => JsError(ex.getMessage)
      }
    }
    val writes = Writes[ConfidenceLevel] { level => JsNumber(level.level) }
    Format(reads, writes)
  }
}
