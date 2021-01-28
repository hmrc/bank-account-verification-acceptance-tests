package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.acceptance.models.auth._

object GGAuth {
  implicit val jsonFormat: OFormat[GGAuth] = Json.format[GGAuth]
}

case class GGAuth(credId: CredId,
                  affinityGroup: AffinityGroup,
                  confidenceLevel: Option[ConfidenceLevel],
                  credentialStrength: CredentialStrength,
                  credentialRole: Option[CredentialRole],
                  enrolments: Seq[Enrolment]) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}

