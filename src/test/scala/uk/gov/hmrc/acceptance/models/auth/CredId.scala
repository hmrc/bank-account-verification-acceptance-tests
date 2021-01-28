package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json.{JsString, JsSuccess, Reads, Writes}

case class CredId(value: String)

object CredId {
  implicit val reads: Reads[CredId] = Reads[CredId] { json => JsSuccess(CredId(json.as[String])) }
  implicit val writes: Writes[CredId] = Writes[CredId] { credId => JsString(credId.value) }
}
