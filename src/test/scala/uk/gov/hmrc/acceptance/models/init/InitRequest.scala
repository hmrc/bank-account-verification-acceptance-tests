package uk.gov.hmrc.acceptance.models.init

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.init.InitRequest.{DEFAULT_MESSAGES, DEFAULT_SERVICE_IDENTIFIER}
import uk.gov.hmrc.acceptance.models.{Address, Messages}

object InitRequest {
  val DEFAULT_SERVICE_IDENTIFIER = "bavf-acceptance-test"
  val DEFAULT_MESSAGES: Messages = Messages(en = Json.obj("service.name" -> DEFAULT_SERVICE_IDENTIFIER), None)

  implicit val initJsonFormat: OFormat[InitRequest] = Json.format[InitRequest]
}

case class InitRequest(serviceIdentifier: String = DEFAULT_SERVICE_IDENTIFIER,
                       continueUrl: String = s"${TestConfig.url("bank-account-verification-frontend-example")}/done",
                       prepopulatedData: Option[PrepopulatedData] = None,
                       address: Option[Address] = None,
                       messages: Option[Messages] = Some(DEFAULT_MESSAGES),
                       customisationsUrl: Option[String] = None,
                       timeoutConfig: Option[InitRequestTimeoutConfig] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }

}
