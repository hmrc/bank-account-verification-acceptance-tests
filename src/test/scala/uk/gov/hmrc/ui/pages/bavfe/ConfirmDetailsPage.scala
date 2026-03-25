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

package uk.gov.hmrc.ui.pages.bavfe

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.titleContains
import uk.gov.hmrc.ui.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.ui.pages.BasePage

case class ConfirmDetailsPage() extends BasePage {

  private def getConfirmationDataForEntryCalled(entry: String): By =
    By.xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd")

  private def getChangeLinkDataForEntryCalled(entry: String): By =
    By.xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd[2]/a")

  def getAccountName: String =
    getText(getConfirmationDataForEntryCalled("Name on the account"))

  def changeAccountName(): Unit =
    click(getChangeLinkDataForEntryCalled("Account name"))

  def getCompanyName: String =
    getText(getConfirmationDataForEntryCalled("Name on the account"))

  def changeCompanyName(): Unit =
    click(getChangeLinkDataForEntryCalled("Company name"))

  def getCompanyRegistrationNumber: String =
    getText(getConfirmationDataForEntryCalled("Company registration number"))

  def changeCompanyRegistrationNumber(): Unit =
    click(getChangeLinkDataForEntryCalled("Company registration number"))

  def getAccountType: String =
    getText(getConfirmationDataForEntryCalled("Account type"))

  def getSortCode: String =
    getText(getConfirmationDataForEntryCalled("Sort code"))

  def changeSortCode(): Unit =
    click(getChangeLinkDataForEntryCalled("Sort code"))

  def getAccountNumber: String =
    getText(getConfirmationDataForEntryCalled("Account number"))

  def changeAccountNumber(): Unit =
    click(getChangeLinkDataForEntryCalled("Account number"))

  def getRollNumber: String =
    getText(getConfirmationDataForEntryCalled("Roll number"))

  def changeRollNumber(): Unit =
    click(getChangeLinkDataForEntryCalled("Roll number"))

  override def isOnPage: Boolean =
    untilTrue(titleContains(s"Check the account details - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK"))

}
