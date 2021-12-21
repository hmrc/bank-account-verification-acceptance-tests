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

package uk.gov.hmrc.acceptance.pages.bavfe

import uk.gov.hmrc.acceptance.pages.BasePage

case class TimeoutDialoguePartial() extends BasePage {

  private lazy val timeoutDialogue: IdQuery = id("hmrc-timeout-dialog")
  private lazy val staySignedInButton: IdQuery = id("hmrc-timeout-keep-signin-btn")
  private lazy val timeoutSignOutLink: IdQuery = id("hmrc-timeout-sign-out-link")

  def isVisible: Boolean = {
    timeoutDialogue.webElement.isDisplayed
  }

  def clickStaySignedIn(): Unit = {
    click on staySignedInButton
  }

  def clickTimeoutSignOut(): Unit = {
    click on timeoutSignOutLink
  }

}
