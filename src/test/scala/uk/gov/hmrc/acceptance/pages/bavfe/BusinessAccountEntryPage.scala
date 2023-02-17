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

package uk.gov.hmrc.acceptance.pages.bavfe

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.acceptance.pages.BasePage

case class BusinessAccountEntryPage() extends BasePage {

  private lazy val pageHeading                 = cssSelector("h1")
  private lazy val companyNameLabel            = cssSelector("label[for=companyName]")
  private lazy val companyNameField: TextField = textField(id("companyName"))
  private lazy val sortCodeLabel               = cssSelector("label[for=sortCode]")
  private lazy val sortCodeHint                = id("sortCode-hint")
  private lazy val sortCodeField               = textField(id("sortCode"))
  private lazy val accountNumberLabel          = cssSelector("label[for=accountNumber]")
  private lazy val accountNumberHint           = id("accountNumber-hint")
  private lazy val accountNumberField          = textField(id("accountNumber"))
  private lazy val rollNumberLabel             = cssSelector("label[for=rollNumber]")
  private lazy val rollNumberHint              = id("rollNumber-hint")
  private lazy val rollNumberField             = textField(id("rollNumber"))
  private lazy val continueButton: IdQuery     = id("continue")

  def enterCompanyName(companyName: String): BusinessAccountEntryPage = {
    companyNameField.value = companyName
    this
  }

  def enterSortCode(sortCode: String): BusinessAccountEntryPage = {
    sortCodeField.value = sortCode
    this
  }

  def enterAccountNumber(accountNumber: String): BusinessAccountEntryPage = {
    accountNumberField.value = accountNumber
    this
  }

  def enterRollNumber(rollNumber: String): BusinessAccountEntryPage = {
    rollNumberField.value = rollNumber
    this
  }

  def getHeading: String =
    pageHeading.webElement.getText

  def getCompanyNameLabel: String =
    companyNameLabel.webElement.getText

  def getSortCodeLabel: String =
    sortCodeLabel.webElement.getText

  def getSortCodeHint: String =
    sortCodeHint.webElement.getText

  def getAccountNumberLabel: String =
    accountNumberLabel.webElement.getText

  def getAccountNumberHint: String =
    accountNumberHint.webElement.getText

  def getRollNumberLabel: String =
    rollNumberLabel.webElement.getText

  def getRollNumberHint: String =
    rollNumberHint.webElement.getText

  def clickContinue(): Unit =
    click on continueButton

  override def isOnPage: Boolean =
    webDriverWillWait.until(titleIs(s"Bank or building society account details - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK"))
}
