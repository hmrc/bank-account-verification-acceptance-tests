package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class AuthStubPage() extends BasePage {

  private lazy val credentialsIdentifierField: TextField = textField(id("authorityId"))
  private lazy val RedirectUrlField: TextField = textField(id("redirectionUrl"))
  private lazy val submitField = xpath("//input[@type='submit']")

  def enterCredID(credId: String): AuthStubPage = {
    credentialsIdentifierField.value = credId
    this
  }

  def enterRedirectUrl(redirectUrl: String): AuthStubPage = {
    RedirectUrlField.value = redirectUrl
    this
  }

  def submit(): Unit = {
    click on submitField
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Authority Wizard"))
  }
}
