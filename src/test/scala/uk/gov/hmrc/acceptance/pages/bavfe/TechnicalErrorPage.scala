package uk.gov.hmrc.acceptance.pages.bavfe

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class TechnicalErrorPage() extends BasePage {

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Error - GOV.UK"))
  }
}
