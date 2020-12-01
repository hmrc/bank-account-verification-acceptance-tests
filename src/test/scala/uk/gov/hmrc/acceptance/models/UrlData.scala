package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object UrlData {
  implicit val jsonFormat: OFormat[UrlData] = Json.format[UrlData]
}

case class UrlData(code: String,
                   method: String,
                   id: String,
                   time: String,
                   url: String) {
}
