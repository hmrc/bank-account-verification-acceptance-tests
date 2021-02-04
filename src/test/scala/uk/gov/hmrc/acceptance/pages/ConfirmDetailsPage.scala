package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.models.InitRequest.DEFAULT_SERVICE_IDENTIFIER

case class ConfirmDetailsPage() extends BasePage {

  private lazy val continueButton: IdQuery = id("continue")

  private def getConfirmationDataForEntryCalled(entry: String): Option[Element] = {
    xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd").findElement
  }

  private def getChangeLinkDataForEntryCalled(entry: String): XPathQuery = {
    xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd[2]/a")
  }

  def getAccountName: String = {
    getConfirmationDataForEntryCalled("Name on the account").get.text
  }

  def changeAccountName(): Unit = {
    click on getChangeLinkDataForEntryCalled("Account name")
  }

  def getCompanyName: String = {
    getConfirmationDataForEntryCalled("Name on the account").get.text
  }

  def changeCompanyName(): Unit = {
    click on getChangeLinkDataForEntryCalled("Company name")
  }

  def getCompanyRegistrationNumber: String = {
    getConfirmationDataForEntryCalled("Company registration number").get.text
  }

  def changeCompanyRegistrationNumber(): Unit = {
    click on getChangeLinkDataForEntryCalled("Company registration number")
  }

  def getAccountType: String = {
    getConfirmationDataForEntryCalled("Account type").get.text
  }

  def getSortCode: String = {
    getConfirmationDataForEntryCalled("Sort code").get.text
  }

  def changeSortCode(): Unit = {
    click on getChangeLinkDataForEntryCalled("Sort code")
  }

  def getAccountNumber: String = {
    getConfirmationDataForEntryCalled("Account number").get.text
  }

  def changeAccountNumber(): Unit = {
    click on getChangeLinkDataForEntryCalled("Account number")
  }

  def getRollNumber: String = {
    getConfirmationDataForEntryCalled("Roll number").get.text
  }

  def changeRollNumber(): Unit = {
    click on getChangeLinkDataForEntryCalled("Roll number")
  }

  def clickContinue(): Unit = {
    click on continueButton
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs(s"Check the account details - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK"))
  }

}
