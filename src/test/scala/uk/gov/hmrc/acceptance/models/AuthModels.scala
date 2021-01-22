package uk.gov.hmrc.acceptance.models

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

case class EnrolmentIdentifier(key: String, value: String)

case class Enrolment(key: String, identifiers: Seq[EnrolmentIdentifier], state: String)

object Enrolment {
  implicit val enrolmentIdentifierFormat = Json.format[EnrolmentIdentifier]
  implicit val format = Json.format[Enrolment]
}

sealed trait AffinityGroup

object AffinityGroup {

  case object Individual extends AffinityGroup

  case object Organisation extends AffinityGroup

  case object Agent extends AffinityGroup

  def isValid(name: String): Boolean = Try(AffinityGroup(name)).isSuccess

  def apply(name: String): AffinityGroup = {
    name match {
      case "Individual" => Individual
      case "Organisation" => Organisation
      case "Agent" => Agent
      case _ => throw new Exception(s"Invalid affinity group: $name")
    }
  }

  implicit val reads = Reads[AffinityGroup] { json =>
    val nameString = json.as[String]
    if (AffinityGroup.isValid(nameString)) JsSuccess(AffinityGroup(nameString))
    else JsError(s"Unsupported AffinityGroup: $nameString")
  }
  implicit val writes = Writes[AffinityGroup] { affinityGroup => JsString(affinityGroup.toString) }
}

sealed trait CredentialRole

object CredentialRole {

  case object User extends CredentialRole

  case object Admin extends CredentialRole

  case object Assistant extends CredentialRole

  def isValid(role: String): Boolean = Try(CredentialRole(role)).isSuccess

  def apply(role: String): CredentialRole = {
    role.toLowerCase match {
      case "user" => User
      case "admin" => Admin
      case "assistant" => Assistant
      case _ => throw new Exception(s"Invalid role: $role")
    }
  }

  implicit val reads = Reads[CredentialRole] { json =>
    val roleString = json.as[String]
    if (CredentialRole.isValid(roleString)) JsSuccess(CredentialRole(roleString))
    else JsError(s"Unsupported CredentialRole: $roleString")
  }
  implicit val writes = Writes[CredentialRole] { role => JsString(role.toString) }
}

case class CredId(value: String)

object CredId {
  implicit val reads = Reads[CredId] { json => JsSuccess(CredId(json.as[String])) }
  implicit val writes = Writes[CredId] { credId => JsString(credId.value) }
}
