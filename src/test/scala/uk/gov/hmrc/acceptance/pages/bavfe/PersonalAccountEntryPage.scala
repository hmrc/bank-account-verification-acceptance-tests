package uk.gov.hmrc.acceptance.pages.bavfe

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.acceptance.pages.BasePage

case class PersonalAccountEntryPage() extends BasePage {

  private lazy val accountNameField: TextField = textField(id("accountName"))
  private lazy val sortCodeField: TextField = textField(id("sortCode"))
  private lazy val accountNumberField: TextField = textField(id("accountNumber"))
  private lazy val rollNumberField: TextField = textField(id("rollNumber"))
  private lazy val continueButton: IdQuery = id("continue")

  def enterAccountName(accountName: String): PersonalAccountEntryPage = {
    accountNameField.value = accountName
    this
  }

  def enterSortCode(sortCode: String): PersonalAccountEntryPage = {
    sortCodeField.value = sortCode
    this
  }

  def enterAccountNumber(accountNumber: String): PersonalAccountEntryPage = {
    accountNumberField.value = accountNumber
    this
  }

  def enterRollNumber(rollNumber: String): PersonalAccountEntryPage = {
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
