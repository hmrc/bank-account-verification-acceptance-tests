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

package uk.gov.hmrc.ui.pages.auth

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.titleContains
import uk.gov.hmrc.ui.pages.BasePage

case class GGAuthStubPage() extends BasePage {

  def enterCredID(credId: String): GGAuthStubPage = {
    find(By.id("authorityId")).sendKeys(credId)
    this
  }

  def enterRedirectUrl(redirectUrl: String): GGAuthStubPage = {
    find(By.id("redirectionUrl")).sendKeys(redirectUrl)
    this
  }

  def submit(): Unit =
    click(By.xpath("//input[@type='submit']"))

  override def isOnPage: Boolean =
    untilTrue(titleContains("Authority Wizard"))
}
