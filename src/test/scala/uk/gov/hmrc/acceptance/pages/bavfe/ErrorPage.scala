package uk.gov.hmrc.acceptance.pages.bavfe

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class ErrorPage() extends BasePage {

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Sorry, we are experiencing technical difficulties - 500 - - GOV.UK"))
  }
}
