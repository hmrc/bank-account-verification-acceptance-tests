package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{By, WebElement}
import org.scalatest.Assertion
import uk.gov.hmrc.acceptance.spec.BaseSpec

trait BasePage extends BaseSpec {

  lazy val webDriverWillWait: WebDriverWait = new WebDriverWait(webDriver, 5, 250)

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

  def assertErrorSummaryLink(elementIdentifier: String, expectedErrorMessage: Option[String] = None): Unit = {
    val linkToError = cssSelector(s"a[href*='$elementIdentifier']")
    expectedErrorMessage match {
      case None =>
        assert(linkToError.findAllElements.isEmpty, s": Error link to $elementIdentifier is displayed when it should not be!")
      case _ =>
        assert(linkToError.findElement.get.isDisplayed, s"Error link to $elementIdentifier is not displayed!")
        assert(linkToError.findElement.get.text.equals(expectedErrorMessage.get), s"\n\nExpected: ${expectedErrorMessage.get}\nGot: ${linkToError.findElement.get.text}\n\n")
    }
  }

  def assertErrorMessage(elementIdentifier: String, expectedErrorMessage: Option[String] = None): Assertion = {
    val errorMessage = id(s"$elementIdentifier-error")
    val dataEntryField = id(s"$elementIdentifier").findElement.get
    val errorBorderClass = "govuk-input--error"
    webDriverWillWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".govuk-error-summary")))
    expectedErrorMessage match {
      case None =>
        assert(errorMessage.findAllElements.isEmpty, s": Error message for $elementIdentifier is displayed when it should not be!")
        assert(!dataEntryField.attribute("class").get.contains(errorBorderClass), s"$elementIdentifier is highlighted when it should not be!")
      case _ =>
        assert(errorMessage.findElement.get.isDisplayed, s"Error message for $elementIdentifier is not displayed!")
        assert(errorMessage.findElement.get.text.equals(s"Error:\n${expectedErrorMessage.get}"), s"\n\nExpected: ${expectedErrorMessage.get}\nGot: ${errorMessage.findElement.get.text}\n\n")
        assert(dataEntryField.attribute("class").get.contains(errorBorderClass), s"$elementIdentifier is not being highlighted!")
    }
  }

  def assertErrorMessageSummaryCountIsEqualTo(expectedErrorCount: Int): Unit = {
    val errorSummaryCount = cssSelector(".govuk-error-summary__list > li").findAllElements.length
    assert(errorSummaryCount.equals(expectedErrorCount), s"Expected $expectedErrorCount errors in summary list, found $errorSummaryCount!")
  }
}
