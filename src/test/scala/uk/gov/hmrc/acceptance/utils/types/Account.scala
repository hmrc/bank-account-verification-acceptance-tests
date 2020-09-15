package uk.gov.hmrc.acceptance.utils.types

import play.api.libs.json.{Json, OFormat}

object Account {
  implicit val jsonFormat: OFormat[Account] = Json.format[Account]
}

case class Account(sortCode: String, accountNumber: String) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
