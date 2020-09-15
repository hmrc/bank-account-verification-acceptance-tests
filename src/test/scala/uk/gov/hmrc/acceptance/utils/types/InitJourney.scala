package uk.gov.hmrc.acceptance.utils.types

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.acceptance.config.TestConfig

object InitJourney {
  implicit val initJsonFormat: OFormat[InitJourney] = Json.format[InitJourney]
}

case class InitJourney(serviceIdentifier: String = "bavf-acceptance-test",
                       continueUrl: String = s"${TestConfig.url("bank-account-verification-frontend-example")}/done",
                       address: Option[Address] = None,
                       messages: Option[Messages] = None,
                       customisationsUrl: Option[String] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }

}
