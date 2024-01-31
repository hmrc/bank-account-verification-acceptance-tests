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

package uk.gov.hmrc.ui.stubs.transunion

class BankCheckEnhancedBuilder {

  private var result             = "Pass"
  private var score              = "7"
  private var accountIssuer      = "HSBC BANK PLC"
  private var otherAccountsFound = "yes"
  private var accountStartDate   = "1999-10-13"

  def result(result: String): BankCheckEnhancedBuilder = {
    this.result = result
    this
  }

  def score(score: String): BankCheckEnhancedBuilder = {
    this.score = score
    this
  }

  def accountIssuer(accountIssuer: String): BankCheckEnhancedBuilder = {
    this.accountIssuer = accountIssuer
    this
  }

  def otherAccountsFound(otherAccountsFound: String): BankCheckEnhancedBuilder = {
    this.otherAccountsFound = otherAccountsFound
    this
  }

  def accountStartDate(accountStartDate: String): BankCheckEnhancedBuilder = {
    this.accountStartDate = accountStartDate
    this
  }

  def build(): String =
    s"""<BankcheckEnhanced>
       |  <Result>$result</Result>
       |  <Score>$score</Score>
       |  <AccountIssuer>$accountIssuer</AccountIssuer>
       |  <OtherAccountsFoundForIssuer>$otherAccountsFound</OtherAccountsFoundForIssuer>
       |  <AccountStartDate>$accountStartDate</AccountStartDate>
       |</BankcheckEnhanced>""".stripMargin
}
