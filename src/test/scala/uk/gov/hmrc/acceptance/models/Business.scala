package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object Business {
  implicit val jsonFormat: OFormat[Business] = Json.format[Business]
}

case class Business(companyName: String, address: Option[Address] = None) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
