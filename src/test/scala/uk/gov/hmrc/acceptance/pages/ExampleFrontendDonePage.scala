package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.utils.BasePage

case class ExampleFrontendDonePage() extends BasePage {

  private lazy val accountType: XPathQuery = xpath("//dt[normalize-space()='Account Type']/following-sibling::dd")
  private lazy val accountName: XPathQuery = xpath("//dt[normalize-space()='Account name']/following-sibling::dd")
  private lazy val sortCode: XPathQuery = xpath("//dt[normalize-space()='Sort code']/following-sibling::dd")
  private lazy val accountNumber: XPathQuery = xpath("//dt[normalize-space()='Account number']/following-sibling::dd")
  private lazy val rollNumber: XPathQuery = xpath("//dt[normalize-space()='Roll number']/following-sibling::dd")
  private lazy val validationResult: XPathQuery = xpath("//dt[normalize-space()='Validation result']/following-sibling::dd")
  private lazy val accountExists: XPathQuery = xpath("//dt[normalize-space()='Account exists']/following-sibling::dd")
  private lazy val accountNameMatched: XPathQuery = xpath("//dt[normalize-space()='Account name matched']/following-sibling::dd")
  private lazy val accountNonConsented: XPathQuery = xpath("//dt[normalize-space()='Account non-consented']/following-sibling::dd")
  private lazy val accountOwnerDeceased: XPathQuery = xpath("//dt[normalize-space()='Account owner deceased']/following-sibling::dd")

  def getAccountType: String = {
    accountType.findElement.get.text
  }

  def getAccountName: String = {
    accountName.findElement.get.text
  }

  def getSortCode: String = {
    sortCode.findElement.get.text
  }

  def getAccountNumber: String = {
    accountNumber.findElement.get.text
  }

  def getRollNumber: String = {
    rollNumber.findElement.get.text
  }

  def getValidationResult: String = {
    validationResult.findElement.get.text
  }

  def getAccountExists: String = {
    accountExists.findElement.get.text
  }

  def getAccountNameMatched: String = {
    accountNameMatched.findElement.get.text
  }

  def getAccountNonConsented: String = {
    accountNonConsented.findElement.get.text
  }

  def getAccountOwnerDeceased: String = {
    accountOwnerDeceased.findElement.get.text
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("bank-account-verification-example-frontend"))
  }
}
