package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class ExampleFrontendHomePage() extends BasePage {

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("bank-account-verification-example-frontend"))
  }
}
