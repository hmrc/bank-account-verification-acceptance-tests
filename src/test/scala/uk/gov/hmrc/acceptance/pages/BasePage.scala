/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.acceptance.pages

import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import org.openqa.selenium.{By, WebElement}
import uk.gov.hmrc.acceptance.utils.BrowserDriver

trait BasePage extends BrowserDriver {

  private lazy val pageHeading: Option[Element] = find(id("pageHeading"))
  private lazy val backLink: IdQuery = id("back")
  private lazy val selectEnglish: CssSelectorQuery = cssSelector("a[hreflang=en]")
  private lazy val selectWelsh: CssSelectorQuery = cssSelector("a[hreflang=cy]")

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
    webDriverWillWait.until(ExpectedConditions.elementToBeClickable(backLink.by))
    click on backLink
  }

  def switchToWelsh(): Unit = {
    click on selectWelsh
  }

  def switchToEnglish(): Unit = {
    click on selectEnglish
  }

  def errorMessageSummaryCount(): Int = {
    webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".govuk-error-summary__list")))
    cssSelector(".govuk-error-summary__list > li").findAllElements.length
  }
}
