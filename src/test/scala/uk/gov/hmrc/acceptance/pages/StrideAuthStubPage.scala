package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class StrideAuthStubPage() extends BasePage {

  private lazy val credentialsIdentifierField: TextField = textField(name("clientId"))
  private lazy val submitField = xpath("//input[@type='submit']")

  def enterClientID(credId: String): StrideAuthStubPage = {
    credentialsIdentifierField.value = credId
    this
  }

  def submit(): Unit = {
    click on submitField
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Authority Wizard"))
  }
}
