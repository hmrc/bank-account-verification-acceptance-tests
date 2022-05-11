/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.acceptance.utils

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.{By, WebElement}

trait CommonAssertions extends BrowserDriver {

  def assertThatErrorSummaryLinkExists(elementIdentifier: String): Unit = {
    webDriverWillWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".govuk-error-summary")))
    val errorSummary: WebElement = cssSelector(s"a[href*='$elementIdentifier']").findElement.get.underlying
    assertThat(errorSummary.isDisplayed).isTrue
    assertThat(errorSummary.getText).isNotEmpty
  }

  def assertThatInputFieldErrorMessageExists(elementIdentifier: String): Unit = {
    val dataEntryField           = id(s"$elementIdentifier").findElement.get
    val errorBorderClass         = "govuk-input--error"
    val errorMessageLocator      = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement =
      webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
    assertThat(dataEntryField.attribute("class").get).contains(errorBorderClass)
  }

  def assertThatRadioButtonErrorMessageIsDisplayed(elementIdentifier: String): Unit = {
    val errorMessageLocator      = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement =
      webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
  }
}
