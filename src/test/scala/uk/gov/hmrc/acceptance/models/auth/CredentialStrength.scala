package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

sealed abstract class CredentialStrength(val name: String)

object CredentialStrength {

  case object Strong extends CredentialStrength("strong")

  case object Weak extends CredentialStrength("weak")

  case object None extends CredentialStrength("none")

  def fromString(credentialStrength: String): CredentialStrength = credentialStrength match {
    case Strong.name => Strong
    case Weak.name => Weak
    case None.name => None
    case _ => throw new NoSuchElementException(s"Illegal credential strength: $credentialStrength")
  }

  implicit val format: Format[CredentialStrength] = {
    val reads = Reads[CredentialStrength] { json =>
      Try {
        fromString(json.as[String])
      } match {
        case Success(credStrength) => JsSuccess(credStrength)
        case Failure(ex) => JsError(ex.getMessage)
      }
    }
    val writes = Writes[CredentialStrength] { credStrength => JsString(credStrength.name) }
    Format(reads, writes)
  }

}
