/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json.{Json, OFormat, OWrites, Reads}

object Address {
  implicit val addressJsonFormat: OFormat[Address] = Json.format[Address]
  implicit val writes: OWrites[Address] = Json.writes[Address]
  implicit val reads: Reads[Address] = Json.reads[Address]
}

case class Address(lines: List[String],
                   town: Option[String] = None,
                   postcode: Option[String] = None) {

  def lineOne(): String = {
    lines.head
  }

  def asStringWithCR(): String = {
    val addressAsString = new StringBuilder
    for (entry <- lines.indices) {
      if (entry == 0) {
        addressAsString.append(lines(entry))
      } else {
        addressAsString.append(s"\n${lines(entry)}")
      }
    }
    addressAsString
      .append(insertPrefix(town, "\n"))
      .append(insertPrefix(postcode, "\n"))
      .mkString
  }

  def asStringWithCommas(): String = {
    val addressAsString = new StringBuilder
    for (entry <- lines.indices) {
      if (entry == 0) {
        addressAsString.append(lines(entry))
      } else {
        addressAsString.append(s", ${lines(entry)}")
      }
    }
    addressAsString
      .append(insertPrefix(town, ", "))
      .append(insertPrefix(postcode, ", "))
      .mkString
  }

  def insertPrefix(item: Option[String], character: String): String = {
    item match {
      case Some(value) => s"$character$value"
      case None => ""
    }
  }
}
