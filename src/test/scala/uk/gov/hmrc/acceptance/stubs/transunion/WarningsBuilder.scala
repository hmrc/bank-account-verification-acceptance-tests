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

package uk.gov.hmrc.acceptance.stubs.transunion

class WarningsBuilder {

  private var nonGBRCardWarning = false
  private var namePicklistWarning = false
  private var addressPicklistWarning = false
  private var pafNonValidWarning = false
  private var cardAccountClosedWarning = false
  private var bankAccountClosedWarning = false

  def nonGBRCardWarning(nonGBRCardWarning: Boolean): WarningsBuilder = {
    this.nonGBRCardWarning = nonGBRCardWarning
    this
  }

  def namePicklistWarning(namePicklistWarning: Boolean): WarningsBuilder = {
    this.namePicklistWarning = namePicklistWarning
    this
  }

  def addressPicklistWarning(addressPicklistWarning: Boolean): WarningsBuilder = {
    this.addressPicklistWarning = addressPicklistWarning
    this
  }

  def pafNonValidWarning(pafNonValidWarning: Boolean): WarningsBuilder = {
    this.pafNonValidWarning = pafNonValidWarning
    this
  }

  def cardAccountClosedWarning(cardAccountClosedWarning: Boolean): WarningsBuilder = {
    this.cardAccountClosedWarning = cardAccountClosedWarning
    this
  }

  def bankAccountClosedWarning(bankAccountClosedWarning: Boolean): WarningsBuilder = {
    this.bankAccountClosedWarning = bankAccountClosedWarning
    this
  }

  def build(): String = {
    s"""<Warnings>
       |  <NonGBRCardWarning>$nonGBRCardWarning</NonGBRCardWarning>
       |  <NamePicklistWarning>$namePicklistWarning</NamePicklistWarning>
       |  <AddressPicklistWarning>$addressPicklistWarning</AddressPicklistWarning>
       |  <PAFNonValidWarning>$pafNonValidWarning</PAFNonValidWarning>
       |  <CardAccountClosedWarning>$cardAccountClosedWarning</CardAccountClosedWarning>
       |  <BankAccountClosedWarning>$bankAccountClosedWarning</BankAccountClosedWarning>
       |</Warnings>""".stripMargin
  }
}
