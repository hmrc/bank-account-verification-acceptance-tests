package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.models.InitRequest.DEFAULT_SERVICE_IDENTIFIER

case class BusinessAccountEntryPage() extends BasePage {

  private lazy val companyNameField: TextField = textField(id("companyName"))
  private lazy val sortCodeField: TextField = textField(id("sortCode"))
  private lazy val accountNumberField: TextField = textField(id("accountNumber"))
  private lazy val rollNumberField: TextField = textField(id("rollNumber"))
  private lazy val continueButton: IdQuery = id("continue")

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

  def clickContinue(): Unit = {
    click on continueButton
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs(s"Bank or building society account details - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK"))
  }
}
