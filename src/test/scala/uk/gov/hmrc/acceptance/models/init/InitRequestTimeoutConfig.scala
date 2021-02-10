package uk.gov.hmrc.acceptance.models.init

import play.api.libs.json.{Json, OFormat}

object InitRequestTimeoutConfig {
  implicit val jsonFormat: OFormat[InitRequestTimeoutConfig] = Json.format[InitRequestTimeoutConfig]
}

case class InitRequestTimeoutConfig(
                                     timeoutUrl: String,
                                     timeoutAmount: Int,
                                     timeoutKeepAliveUrl: Option[String] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
