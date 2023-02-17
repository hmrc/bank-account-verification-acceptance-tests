/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object Account {
  implicit val jsonFormat: OFormat[Account] = Json.format[Account]
}

case class Account(
  sortCode: String,
  accountNumber: String,
  rollNumber: Option[String] = None,
  bankName: Option[String] = None,
  iban: Option[String] = None
) {
  def asJsonString(): String =
    Json.toJson(this).toString()

  def storedSortCode(): String =
    this.sortCode.replaceAll("\\s|-", "")
}
