package uk.gov.hmrc.acceptance.pages.bavfefe

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class ExampleFrontendHomePage() extends BasePage {

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("bank-account-verification-example-frontend"))
  }
}
