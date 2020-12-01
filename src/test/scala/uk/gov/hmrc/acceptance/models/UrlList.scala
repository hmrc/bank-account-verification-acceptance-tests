package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object UrlList {
  implicit val jsonFormat: OFormat[UrlList] = Json.format[UrlList]
}

case class UrlList(urlsByUrlRegex: Array[UrlData]) {
}
