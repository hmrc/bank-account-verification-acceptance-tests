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

package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.JourneyBuilderResponse
import uk.gov.hmrc.acceptance.models.init.{InitRequest, InitRequestTimeoutConfig}
import uk.gov.hmrc.acceptance.pages.bavfe.{SelectAccountTypePage, TechnicalErrorPage, TimeoutDialoguePartial}
import uk.gov.hmrc.acceptance.pages.stubbed.JourneyTimeOutPage
import uk.gov.hmrc.acceptance.utils.MockServer

import java.net.URLEncoder

case class TimeoutSpec() extends BaseSpec with MockServer {

  Scenario("Timeout Dialogue links to relative link") {
    Given("I am on a Bank Account Verification Frontend page")

    val timeoutRelativePath                        = "/admin/metrics"
    val journeyBuilderData: JourneyBuilderResponse = initializeJourneyV1(
      InitRequest(
        timeoutConfig = Some(InitRequestTimeoutConfig(timeoutRelativePath, 120))
      ).asJsonString()
    )

    startGGJourney(journeyBuilderData)
    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a timeout dialogue appears after a period of inactivity")

    assertThat(TimeoutDialoguePartial().isVisible).isTrue

    Then("I click on sign out and I'm sent to a relative URL correctly")

    TimeoutDialoguePartial().clickTimeoutSignOut()
    assertThat(webDriver.getCurrentUrl).isEqualTo(
      s"${TestConfig.getHost("bank-account-verification")}$timeoutRelativePath"
    )
  }

  Scenario("Timeout Dialogue does not link to an absolute URL") {
    Given("I am on a Bank Account Verification Frontend page")

    val timeoutURL                                 = "http://www.google.co.uk"
    val journeyBuilderData: JourneyBuilderResponse = initializeJourneyV1(
      InitRequest(
        timeoutConfig = Some(InitRequestTimeoutConfig(timeoutURL, 120))
      ).asJsonString()
    )

    val session = startGGJourney(journeyBuilderData)
    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a timeout dialogue appears after a period of inactivity")

    assertThat(TimeoutDialoguePartial().isVisible).isTrue

    Then("I click on sign out and I'm sent to a relative URL correctly")

    TimeoutDialoguePartial().clickTimeoutSignOut()
    assertThat(webDriver.getCurrentUrl).isEqualTo(
      s"${TestConfig.url("bank-account-verification")}/destroySession?journeyId=${session.journeyId}&timeoutUrl=${URLEncoder
        .encode(timeoutURL, "UTF-8")}"
    )
    assertThat(TechnicalErrorPage().isOnPage).isTrue
  }

  Scenario("Timeout Dialogue links to an absolute URL on allow list") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("GET")
          .withPath("/timed/out")
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "text/html")
          .withBody(s"""
             |<!DOCTYPE html>
             |<html lang="en">
             |<head>
             |	<meta charset="utf-8">
             |	<title>Timeout</title>
             |</head>
             |<body>
             |	<h1>A Timeout Has Occurred</h1>
             |</body>
             |</html>
             |""".stripMargin)
          .withStatusCode(200)
      )

    Given("I am on a Bank Account Verification Frontend page")

    val timeoutURL                          = s"${TestConfig.environmentHost}:${TestConfig.mockServerPort()}/timed/out"
    val journeyData: JourneyBuilderResponse = initializeJourneyV1(
      InitRequest(
        timeoutConfig = Some(InitRequestTimeoutConfig(timeoutURL, 120))
      ).asJsonString()
    )

    startGGJourney(journeyData)
    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a timeout dialogue appears after a period of inactivity")

    assertThat(TimeoutDialoguePartial().isVisible).isTrue

    Then("I click on sign out and I'm sent to the absolute URL that is on the allow list correctly")

    TimeoutDialoguePartial().clickTimeoutSignOut()

    assertThat(JourneyTimeOutPage().isOnPage).isTrue
  }
}
