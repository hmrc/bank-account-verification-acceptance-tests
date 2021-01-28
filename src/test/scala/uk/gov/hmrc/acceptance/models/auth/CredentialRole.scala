package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}

import scala.util.Try

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

  implicit val reads: Reads[CredentialRole] = Reads[CredentialRole] { json =>
    val roleString = json.as[String]
    if (CredentialRole.isValid(roleString)) JsSuccess(CredentialRole(roleString))
    else JsError(s"Unsupported CredentialRole: $roleString")
  }
  implicit val writes: Writes[CredentialRole] = Writes[CredentialRole] { role => JsString(role.toString) }
}
