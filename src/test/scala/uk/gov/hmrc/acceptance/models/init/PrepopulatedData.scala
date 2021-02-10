package uk.gov.hmrc.acceptance.models.init

import play.api.libs.json.{Json, OFormat}

object PrepopulatedData {
  implicit val jsonFormat: OFormat[PrepopulatedData] = Json.format[PrepopulatedData]
}

case class PrepopulatedData(accountType: String,
                            name: Option[String] = None,
                            sortCode: Option[String] = None,
                            accountNumber: Option[String] = None,
                            rollNumber: Option[String] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }
}
