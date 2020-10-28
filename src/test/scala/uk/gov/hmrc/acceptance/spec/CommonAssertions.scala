package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.{By, WebElement}
import uk.gov.hmrc.acceptance.utils.BrowserDriver

trait CommonAssertions extends BrowserDriver {

  def assertThatErrorSummaryLinkExists(elementIdentifier: String): Unit = {
    webDriverWillWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".govuk-error-summary")))
    val errorSummary: WebElement = cssSelector(s"a[href*='$elementIdentifier']").findElement.get.underlying
    assertThat(errorSummary.isDisplayed).isTrue
    assertThat(errorSummary.getText).isNotEmpty
  }

  def assertThatInputFieldErrorMessageExists(elementIdentifier: String): Unit = {
    val dataEntryField = id(s"$elementIdentifier").findElement.get
    val errorBorderClass = "govuk-input--error"
    val errorMessageLocator = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement = webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
    assertThat(dataEntryField.attribute("class").get).contains(errorBorderClass)
  }

  def assertThatRadioButtonErrorMessageIsDisplayed(elementIdentifier: String): Unit = {
    val errorMessageLocator = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement = webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
  }
}
