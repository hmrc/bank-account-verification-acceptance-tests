package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class ExampleFrontendDonePage() extends BasePage {

  private def buildLocatorForSummaryListEntryCalled(entry: String): XPathQuery = {
    xpath(s"//dt[normalize-space()='$entry']/following-sibling::dd")
  }

  def getAccountType: String = {
    buildLocatorForSummaryListEntryCalled("Account type").findElement.get.text
  }

  def getAccountName: String = {
    buildLocatorForSummaryListEntryCalled("Name on the account").findElement.get.text
  }

  def getSortCode: String = {
    buildLocatorForSummaryListEntryCalled("Sort code").findElement.get.text
  }

  def getAccountNumber: String = {
    buildLocatorForSummaryListEntryCalled("Account number").findElement.get.text
  }

  def getAddress: String = {
    buildLocatorForSummaryListEntryCalled("Address").findElement.get.text
  }

  def getRollNumber: String = {
    buildLocatorForSummaryListEntryCalled("Roll number").findElement.get.text
  }

  def getValidationResult: String = {
    buildLocatorForSummaryListEntryCalled("Validation result").findElement.get.text
  }

  def getAccountExists: String = {
    buildLocatorForSummaryListEntryCalled("Account exists").findElement.get.text
  }

  def getAccountNameMatched: String = {
    buildLocatorForSummaryListEntryCalled("Account name matched").findElement.get.text
  }

  def getAccountAddressMatched: String = {
    buildLocatorForSummaryListEntryCalled("Account address matched").findElement.get.text
  }

  def getAccountNonConsented: String = {
    buildLocatorForSummaryListEntryCalled("Account non-consented").findElement.get.text
  }

  def getAccountOwnerDeceased: String = {
    buildLocatorForSummaryListEntryCalled("Account owner deceased").findElement.get.text
  }

  def getCompanyName: String = {
    buildLocatorForSummaryListEntryCalled("Company name").findElement.get.text
  }

  def getCompanyNameMatches: String = {
    buildLocatorForSummaryListEntryCalled("Company name matches").findElement.get.text
  }

  def getCompanyPostcodeMatches: String = {
    buildLocatorForSummaryListEntryCalled("Company postcode matches").findElement.get.text
  }

  def getBankName: String = {
    buildLocatorForSummaryListEntryCalled("Bank name").findElement.get.text
  }

  def checkEntryDoesNotExistFor(entryName: String): Boolean = {
    buildLocatorForSummaryListEntryCalled(entryName).findAllElements.isEmpty
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("bank-account-verification-example-frontend"))
  }
}
