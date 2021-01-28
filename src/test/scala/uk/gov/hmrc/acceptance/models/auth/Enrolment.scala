package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json.{Json, OFormat}

case class Enrolment(key: String, identifiers: Seq[EnrolmentIdentifier], state: String)

object Enrolment {
  implicit val enrolmentIdentifierFormat: OFormat[EnrolmentIdentifier] = Json.format[EnrolmentIdentifier]
  implicit val format: OFormat[Enrolment] = Json.format[Enrolment]
}
