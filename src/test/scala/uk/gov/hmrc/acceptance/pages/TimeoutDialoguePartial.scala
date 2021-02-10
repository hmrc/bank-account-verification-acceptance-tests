package uk.gov.hmrc.acceptance.pages

case class TimeoutDialoguePartial() extends BasePage {

  private lazy val timeoutDialogue: IdQuery = id("hmrc-timeout-dialog")
  private lazy val staySignedInButton: IdQuery = id("hmrc-timeout-keep-signin-btn")
  private lazy val signOutLink: IdQuery = id("hmrc-timeout-sign-out-link")

  def isVisible: Boolean = {
    timeoutDialogue.webElement.isDisplayed
  }

  def clickStaySignedIn(): Unit = {
    click on staySignedInButton
  }

  def clickSignOut(): Unit = {
    click on signOutLink
  }

}
