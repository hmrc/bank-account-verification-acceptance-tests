package uk.gov.hmrc.acceptance.pages.bavfefe

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class ExtraInformationPage() extends BasePage {

  private lazy val continueButton: NameQuery = name("continue")

  def clickContinue(): Unit = {
    click on continueButton
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("bank-account-verification-example-frontend"))
  }
}
