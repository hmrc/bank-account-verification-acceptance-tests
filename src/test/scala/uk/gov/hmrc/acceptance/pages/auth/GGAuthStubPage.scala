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

package uk.gov.hmrc.acceptance.pages.auth

import org.openqa.selenium.support.ui.ExpectedConditions.titleIs
import uk.gov.hmrc.acceptance.pages.BasePage

case class GGAuthStubPage() extends BasePage {

  private lazy val credentialsIdentifierField: TextField = textField(id("authorityId"))
  private lazy val RedirectUrlField: TextField = textField(id("redirectionUrl"))
  private lazy val submitField = xpath("//input[@type='submit']")

  def enterCredID(credId: String): GGAuthStubPage = {
    credentialsIdentifierField.value = credId
    this
  }

  def enterRedirectUrl(redirectUrl: String): GGAuthStubPage = {
    RedirectUrlField.value = redirectUrl
    this
  }

  def submit(): Unit = {
    click on submitField
  }

  override def isOnPage: Boolean = {
    webDriverWillWait.until(titleIs("Authority Wizard"))
  }
}
