package uk.gov.hmrc.acceptance.pages.auth

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class StrideAuthStubPage() extends BasePage {

  private lazy val credentialsIdentifierField: TextField = textField(name("clientId"))
  private lazy val submitField = id("submit")

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
