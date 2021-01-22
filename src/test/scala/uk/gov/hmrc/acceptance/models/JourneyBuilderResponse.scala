package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object JourneyBuilderResponse {
  implicit val jsonFormat: OFormat[JourneyBuilderResponse] = Json.format[JourneyBuilderResponse]
}

case class JourneyBuilderResponse(initResponse: InitResponse,
                                  credId: String) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
