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

  def assertErrorSummaryLinkExists(elementIdentifier: String): Unit = {
    val linkToError = cssSelector(s"a[href*='$elementIdentifier']")
    webDriverWillWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".govuk-error-summary")))
    assert(linkToError.findElement.get.isDisplayed, s"Error link to $elementIdentifier is not displayed!")
    assert(!linkToError.findElement.get.text.isEmpty, s"\n\nExpected a link to an error with some text, could not find any!\n\n")
  }

  def assertInputFieldErrorMessageExists(elementIdentifier: String): Assertion = {
    val dataEntryField = id(s"$elementIdentifier").findElement.get
    val errorBorderClass = "govuk-input--error"
    val errorMessageLocator = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement = webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assert(errorMessage.isDisplayed, s"Error message for $elementIdentifier is not displayed!")
    assert(!errorMessage.getText.isEmpty, s"\n\nExpected some error text, could not find any!\n\n")
    assert(dataEntryField.attribute("class").get.contains(errorBorderClass), s"$elementIdentifier is not being highlighted!")
  }

  def assertRadioButtonErrorMessageExists(elementIdentifier: String): Assertion = {
    val errorMessageLocator = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement = webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assert(errorMessage.isDisplayed, s"Error message for $elementIdentifier is not displayed!")
    assert(!errorMessage.getText.isEmpty, s"\n\nExpected some error text, could not find any!\n\n")
  }

  def assertErrorMessageSummaryCountIsEqualTo(expectedErrorCount: Int): Unit = {
    val errorSummaryCount = cssSelector(".govuk-error-summary__list > li").findAllElements.length
    assert(errorSummaryCount.equals(expectedErrorCount), s"Expected $expectedErrorCount errors in summary list, found $errorSummaryCount!")
  }
}
