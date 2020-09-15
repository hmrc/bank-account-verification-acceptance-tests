package uk.gov.hmrc.acceptance.stubs.creditsafe

import play.api.libs.json.{Json, OFormat}

object CreditSafePayload {
  implicit val jsonFormat: OFormat[CreditSafePayload] = Json.format[CreditSafePayload]
}

case class CreditSafePayload(
                              sortCode: String,
                              accountNumber: String,
                              businessName: String,
                              postCode: String,
                              companyRegistrationNumber: String
                            ) {
  def asJsonString(): String = {
    Json.toJson(CreditSafePayload(
      this.sortCode.filterNot((x: Char) => x.isWhitespace),
      this.accountNumber,
      this.businessName,
      this.postCode,
      this.companyRegistrationNumber
    )).toString()
  }
}
