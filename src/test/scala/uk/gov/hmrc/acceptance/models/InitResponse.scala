package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object InitResponse {
  implicit val jsonFormat: OFormat[InitResponse] = Json.format[InitResponse]
}

case class InitResponse(journeyId: String,
                        startUrl: String,
                        completeUrl: String,
                        detailsUrl: Option[String] = None) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
