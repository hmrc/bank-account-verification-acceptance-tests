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

package uk.gov.hmrc.ui.pages

import org.openqa.selenium.By

trait BankAccountPage extends BasePage {

  def getSortCodeLabel: String =
    getText(By.cssSelector("label[for=sortCode]"))

  def getSortCodeHint: String =
    getText(By.id("sortCode-hint"))

  def enterSortCode(sortCode: String): BankAccountPage = {
    sendKeys(By.id("sortCode"), sortCode)
    this
  }

  def getAccountNumberLabel: String =
    getText(By.cssSelector("label[for=accountNumber]"))

  def getAccountNumberHint: String =
    getText(By.id("accountNumber-hint"))

  def enterAccountNumber(accountNumber: String): BankAccountPage = {
    sendKeys(By.id("accountNumber"), accountNumber)
    this
  }

  def getRollNumberLabel: String =
    getText(By.cssSelector("label[for=rollNumber]"))

  def getRollNumberHint: String =
    getText(By.id("rollNumber-hint"))

  def enterRollNumber(rollNumber: String): BankAccountPage = {
    sendKeys(By.id("rollNumber"), rollNumber)
    this
  }
}
