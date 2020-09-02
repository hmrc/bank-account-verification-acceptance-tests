package uk.gov.hmrc.acceptance.utils

import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import org.openqa.selenium.{By, WebElement}

trait BasePage extends BaseSpec {

  private lazy val backLink: CssSelectorQuery = cssSelector(".govuk-back-link")
  private lazy val pageHeading: Option[Element] = find(id("pageHeading"))

  def isOnPage: Boolean = false

  def errorSummaryLink(element: String): WebElement = {
    webDriverWillWait.until(visibilityOfElementLocated(By.cssSelector(s"a[href*='$element']")))
  }

  def errorNotificationField(field: String): WebElement = {
    webDriverWillWait.until(visibilityOfElementLocated(By.id(s"$field-error")))
  }

  def getPageHeading: String = {
    pageHeading.get.text
  }

  def clickBackLink(): Unit = {
    click on backLink
  }
}
