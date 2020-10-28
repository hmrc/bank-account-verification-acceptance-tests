package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.InitJourney.DEFAULT_SERVICE_IDENTIFIER

object InitJourney {
  val DEFAULT_SERVICE_IDENTIFIER = "bavf-acceptance-test"
  implicit val initJsonFormat: OFormat[InitJourney] = Json.format[InitJourney]
}

case class InitJourney(serviceIdentifier: String = DEFAULT_SERVICE_IDENTIFIER,
                       continueUrl: String = s"${TestConfig.url("bank-account-verification-frontend-example")}/done",
                       address: Option[Address] = None,
                       messages: Option[Messages] = None,
                       customisationsUrl: Option[String] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }

}
