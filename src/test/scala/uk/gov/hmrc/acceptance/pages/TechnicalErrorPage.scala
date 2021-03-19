package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class TechnicalErrorPage() extends BasePage {

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Error - - GOV.UK"))
  }
}
