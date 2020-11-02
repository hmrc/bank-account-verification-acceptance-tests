package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.InitRequest.DEFAULT_SERVICE_IDENTIFIER

object InitRequest {
  val DEFAULT_SERVICE_IDENTIFIER = "bavf-acceptance-test"
  implicit val initJsonFormat: OFormat[InitRequest] = Json.format[InitRequest]
}

case class InitRequest(serviceIdentifier: String = DEFAULT_SERVICE_IDENTIFIER,
                       continueUrl: String = s"${TestConfig.url("bank-account-verification-frontend-example")}/done",
                       prepopulatedData: Option[PrepopulatedData] = None,
                       address: Option[Address] = None,
                       messages: Option[Messages] = None,
                       customisationsUrl: Option[String] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }

}
