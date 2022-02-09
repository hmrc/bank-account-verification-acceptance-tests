package uk.gov.hmrc.acceptance.models.init

import play.api.libs.json.{Json, OFormat}

object MaxCallConfig {
  implicit val initJsonFormat: OFormat[MaxCallConfig] = Json.format[MaxCallConfig]
}

case class MaxCallConfig(count: Int,
                         redirectUrl: String) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
