package uk.gov.hmrc.acceptance.utils.types

import play.api.libs.json.{Json, OFormat}

object Business {
  implicit val jsonFormat: OFormat[Business] = Json.format[Business]
}

case class Business(companyName: String, address: Option[Address] = None) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
