package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.utils.BasePage

case class SelectAccountTypePage() extends BasePage {

  private lazy val personalAccount: RadioButton = radioButton(id("personal"))
  private lazy val businessAccount: RadioButton = radioButton(id("business"))
  private lazy val continueButton: IdQuery = id("continue")

  def selectPersonalAccount(): SelectAccountTypePage = {
    click on personalAccount
    this
  }

  def selectBusinessAccount(): SelectAccountTypePage = {
    click on businessAccount
    this
  }

  def clickContinue(): Unit = {
    click on continueButton
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Account Type - GOV.UK"))
  }
}
