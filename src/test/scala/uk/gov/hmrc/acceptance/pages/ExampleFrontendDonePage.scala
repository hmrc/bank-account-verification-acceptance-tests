package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.utils.BasePage

case class ExampleFrontendDonePage() extends BasePage {

  private def getDataForSummaryListEntryCalled(entry: String): Option[Element] ={
    xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd").findElement
  }

  def getAccountType: String = {
    getDataForSummaryListEntryCalled("Account type").get.text
  }

  def getAccountName: String = {
    getDataForSummaryListEntryCalled("Name on the account").get.text
  }

  def getSortCode: String = {
    getDataForSummaryListEntryCalled("Sort code").get.text
  }

  def getAccountNumber: String = {
    getDataForSummaryListEntryCalled("Account number").get.text
  }

  def getAddress: String = {
    getDataForSummaryListEntryCalled("Address").get.text
  }

  def getRollNumber: String = {
    getDataForSummaryListEntryCalled("Roll number").get.text
  }

  def getValidationResult: String = {
    getDataForSummaryListEntryCalled("Validation result").get.text
  }

  def getAccountExists: String = {
    getDataForSummaryListEntryCalled("Account exists").get.text
  }

  def getAccountNameMatched: String = {
    getDataForSummaryListEntryCalled("Account name matched").get.text
  }

  def getAccountAddressMatched: String = {
    getDataForSummaryListEntryCalled("Account address matched").get.text
  }

  def getAccountNonConsented: String = {
    getDataForSummaryListEntryCalled("Account non-consented").get.text
  }

  def getAccountOwnerDeceased: String = {
    getDataForSummaryListEntryCalled("Account owner deceased").get.text
  }

  def getCompanyName: String = {
    getDataForSummaryListEntryCalled("Company name").get.text
  }

  def getCompanyNameMatches: String = {
    getDataForSummaryListEntryCalled("Company name matches").get.text
  }
  def getCompanyPostcodeMatches: String = {
    getDataForSummaryListEntryCalled("Company postcode matches").get.text
  }
  def getCompanyRegistrationNumberMatches: String = {
    getDataForSummaryListEntryCalled("Company registration number matches").get.text
  }

  def getBankName: String = {
    getDataForSummaryListEntryCalled("Bank name").get.text
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("bank-account-verification-example-frontend"))
  }
}
