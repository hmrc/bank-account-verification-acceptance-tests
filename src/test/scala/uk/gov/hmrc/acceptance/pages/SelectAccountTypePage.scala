package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs

case class SelectAccountTypePage() extends BasePage {

  private lazy val businessAccount: RadioButton = radioButton(id("accountType"))
  private lazy val personalAccount: RadioButton = radioButton(id("accountType-2"))
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
    webDriverWillWait.until(titleIs("What type of account details are you providing? - GOV.UK"))
  }
}
