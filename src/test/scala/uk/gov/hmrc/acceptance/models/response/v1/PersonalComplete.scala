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

case class PersonalComplete(
  address: Option[Address],
  accountName: String,
  sortCode: String,
  accountNumber: String,
  accountNumberWithSortCodeIsValid: String,
  rollNumber: Option[String] = None,
  accountExists: Option[String] = None,
  nameMatches: Option[String] = None,
  addressMatches: Option[String] = None,
  nonConsented: Option[String] = None,
  subjectHasDeceased: Option[String] = None,
  nonStandardAccountDetailsRequiredForBacs: Option[String] = None,
  sortCodeBankName: Option[String] = None,
  sortCodeSupportsDirectDebit: Option[String] = None,
  sortCodeSupportsDirectCredit: Option[String] = None,
  iban: Option[String] = None
)

object PersonalComplete {
  implicit val writes: OWrites[PersonalComplete] = Json.writes[PersonalComplete]
  implicit val reads: Reads[PersonalComplete]    = Json.reads[PersonalComplete]
}
