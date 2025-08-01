/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ui.pages

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.openqa.selenium.support.ui.ExpectedConditions._
import org.openqa.selenium.support.ui.{ExpectedConditions, FluentWait, Wait}
import org.openqa.selenium.{By, WebDriver, WebElement}
import uk.gov.hmrc.selenium.component.PageObject
import uk.gov.hmrc.selenium.webdriver.Driver

import java.time.Duration
import scala.jdk.CollectionConverters._

trait BasePage extends PageObject {

  def fluentWait(): Wait[WebDriver] = new FluentWait[WebDriver](Driver.instance)
    .withTimeout(Duration.ofSeconds(5))
    .pollingEvery(Duration.ofMillis(100))
    .ignoring(classOf[Exception])

  def navigateTo(url: String): Unit = get(url)

  def currentUrl(): String = getCurrentUrl

  def find(by: By): WebElement =
    fluentWait().until(presenceOfElementLocated(by))

  def findAll(by: By): Seq[WebElement] =
    fluentWait().until(presenceOfAllElementsLocatedBy(by)).asScala.toSeq

  def linkText(text: String): WebElement =
    find(By.linkText(text))

  private lazy val pageHeading: By   = By.id("pageHeading")
  private lazy val backLink: By      = By.id("back")
  private lazy val signOutLink: By   = By.cssSelector(".hmrc-sign-out-nav__link")
  private lazy val selectEnglish: By = By.cssSelector("a[hreflang=en]")
  private lazy val selectWelsh: By   = By.cssSelector("a[hreflang=cy]")

  def isOnPage: Boolean = false

  def errorSummaryLink(element: String): WebElement =
    fluentWait().until(visibilityOfElementLocated(By.cssSelector(s"a[href*='$element']")))

  def errorNotificationField(field: String): WebElement =
    fluentWait().until(visibilityOfElementLocated(By.id(s"$field-error")))

  def isSignOutLinkDisplayed(isDisplayed: Boolean): ObjectAssert[Any] =
    assertThat(if (isDisplayed) {
      fluentWait().until(visibilityOfElementLocated(signOutLink)).isDisplayed
    } else {
      fluentWait().until(invisibilityOfElementLocated(signOutLink))
    })

  def getSignOutLinkLocation: String =
    find(signOutLink).getAttribute("href")

  def getPageHeading: String =
    getText(pageHeading)

  def clickBackLink(): Unit = {
    fluentWait().until(ExpectedConditions.elementToBeClickable(backLink))
    click(backLink)
  }

  def clickSignOut(): Unit =
    click(signOutLink)

  def switchToWelsh(): Unit =
    click(selectWelsh)

  def switchToEnglish(): Unit =
    click(selectEnglish)

  def getHeading: String =
    getText(By.cssSelector("h1"))

  def clickContinue(): Unit =
    click(By.id("continue"))

  def getJourneyId: String =
    getText(By.id("journeyId"))

  def errorMessageSummaryCount(): Int =
    findAll(By.cssSelector(".govuk-error-summary__list > li")).length

  def assertThatErrorSummaryLinkExists(elementIdentifier: String): Unit = {
    val errorSummary: WebElement = find(By.cssSelector(s"a[href*='$elementIdentifier']"))
    assertThat(errorSummary.isDisplayed).isTrue
    assertThat(errorSummary.getText).isNotEmpty
  }

  def assertThatInputFieldErrorMessageExists(elementIdentifier: String): Unit = {
    val dataEntryField = find(By.id(s"$elementIdentifier"))
    val errorMessage   = find(By.id(s"$elementIdentifier-error"))

    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
    assertThat(dataEntryField.getAttribute("class").contains("govuk-input--error"))
  }

  def assertThatRadioButtonErrorMessageIsDisplayed(elementIdentifier: String): Unit = {
    val errorMessage = find(By.id(s"$elementIdentifier-error"))

    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
  }
}
