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

import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.{InitResponse, JourneyBuilderResponse}
import uk.gov.hmrc.acceptance.pages.auth.{GGAuthStubPage, StrideAuthStubPage}

trait CommonActions extends BrowserDriver {

  def startGGJourney(journeyStart: JourneyBuilderResponse): InitResponse = {
    go to s"${TestConfig.url("auth-login-stub")}/gg-sign-in"
    GGAuthStubPage()
      .enterCredID(journeyStart.credId)
      .enterRedirectUrl(s"${TestConfig.getHost("bank-account-verification")}${journeyStart.initResponse.startUrl}")
      .submit()
    journeyStart.initResponse
  }

  def continueGGJourney(journeyStart: JourneyBuilderResponse): Unit = {
    go to s"${TestConfig.url("auth-login-stub")}/gg-sign-in"
    GGAuthStubPage()
      .enterCredID(journeyStart.credId)
      .enterRedirectUrl(
        s"${TestConfig.getHost("bank-account-verification")}${journeyStart.initResponse.detailsUrl.get}"
      )
      .submit()
  }

  def startStrideJourney(journeyStart: JourneyBuilderResponse): InitResponse = {
    go to s"${TestConfig.url("auth-login-stub")}/application-login?continue=${TestConfig.getHost("bank-account-verification")}${journeyStart.initResponse.startUrl}"
    StrideAuthStubPage()
      .enterClientID(journeyStart.credId)
      .submit()
    journeyStart.initResponse
  }

}
