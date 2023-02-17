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

case class ConfirmDetailsPage() extends BasePage {

  private lazy val continueButton: IdQuery = id("continue")

  private def getConfirmationDataForEntryCalled(entry: String): Option[Element] =
    xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd").findElement

  private def getChangeLinkDataForEntryCalled(entry: String): XPathQuery =
    xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd[2]/a")

  def getAccountName: String =
    getConfirmationDataForEntryCalled("Name on the account").get.text

  def changeAccountName(): Unit =
    click on getChangeLinkDataForEntryCalled("Account name")

  def getCompanyName: String =
    getConfirmationDataForEntryCalled("Name on the account").get.text

  def changeCompanyName(): Unit =
    click on getChangeLinkDataForEntryCalled("Company name")

  def getCompanyRegistrationNumber: String =
    getConfirmationDataForEntryCalled("Company registration number").get.text

  def changeCompanyRegistrationNumber(): Unit =
    click on getChangeLinkDataForEntryCalled("Company registration number")

  def getAccountType: String =
    getConfirmationDataForEntryCalled("Account type").get.text

  def getSortCode: String =
    getConfirmationDataForEntryCalled("Sort code").get.text

  def changeSortCode(): Unit =
    click on getChangeLinkDataForEntryCalled("Sort code")

  def getAccountNumber: String =
    getConfirmationDataForEntryCalled("Account number").get.text

  def changeAccountNumber(): Unit =
    click on getChangeLinkDataForEntryCalled("Account number")

  def getRollNumber: String =
    getConfirmationDataForEntryCalled("Roll number").get.text

  def changeRollNumber(): Unit =
    click on getChangeLinkDataForEntryCalled("Roll number")

  def clickContinue(): Unit =
    click on continueButton

  override def isOnPage: Boolean =
    webDriverWillWait.until(titleIs(s"Check the account details - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK"))

}
