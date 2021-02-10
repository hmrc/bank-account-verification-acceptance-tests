package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class ErrorPage() extends BasePage {

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Sorry, we are experiencing technical difficulties - 500 - - GOV.UK"))
  }
}
