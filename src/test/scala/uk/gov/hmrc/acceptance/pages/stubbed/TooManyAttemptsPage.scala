package uk.gov.hmrc.acceptance.pages.stubbed

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class TooManyAttemptsPage() extends BasePage {

  private lazy val journeyId = id("journeyId")

  def getJourneyId(): String = {
    journeyId.findElement.get.text
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Too many attempts"))
  }
}
