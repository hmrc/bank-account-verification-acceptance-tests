/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.acceptance.models.response.v1

import play.api.libs.json.{Json, OWrites, Reads}
import uk.gov.hmrc.acceptance.models.Address

case class BusinessComplete(
  address: Option[Address],
  companyName: String,
  sortCode: String,
  accountNumber: String,
  rollNumber: Option[String] = None,
  accountNumberWithSortCodeIsValid: String,
  accountExists: Option[String] = None,
  companyNameMatches: Option[String],
  companyPostCodeMatches: Option[String],
  companyRegistrationNumberMatches: Option[String],
  nonStandardAccountDetailsRequiredForBacs: Option[String] = None,
  sortCodeBankName: Option[String] = None,
  sortCodeSupportsDirectDebit: Option[String] = None,
  sortCodeSupportsDirectCredit: Option[String] = None
)

object BusinessComplete {
  implicit val writes: OWrites[BusinessComplete] = Json.writes[BusinessComplete]
  implicit val reads: Reads[BusinessComplete]    = Json.reads[BusinessComplete]
}
