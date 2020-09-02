package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.utils.BasePage

case class AccountEntryPage() extends BasePage {

  private lazy val accountNameField: TextField = textField(id("accountName"))
  private lazy val sortCodeField: TextField = textField(id("sortCode"))
  private lazy val accountNumberField: TextField = textField(id("accountNumber"))
  private lazy val rollNumberField: TextField = textField(id("rollNumber"))
  private lazy val continueButton: IdQuery = id("continue")

  def enterAccountName(accountName: String): AccountEntryPage = {
    accountNameField.value = accountName
    this
  }

  def enterSortCode(sortCode: String): AccountEntryPage = {
    sortCodeField.value = sortCode
    this
  }

  def enterAccountNumber(accountNumber: String): AccountEntryPage = {
    accountNumberField.value = accountNumber
    this
  }

  def enterRollNumber(rollNumber: String): AccountEntryPage = {
    rollNumberField.value = rollNumber
    this
  }

  def clickContinue(): Unit = {
    click on continueButton
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Bank Account Verification"))
  }
}
