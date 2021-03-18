package uk.gov.hmrc.acceptance.models.init

import play.api.libs.json.{Json, OFormat}

object InitBACSRequirements {
  implicit val jsonFormat: OFormat[InitBACSRequirements] = Json.format[InitBACSRequirements]
}

case class InitBACSRequirements(directDebitRequired: Boolean, directCreditRequired: Boolean) {
  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
