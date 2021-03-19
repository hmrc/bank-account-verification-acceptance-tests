package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.JourneyBuilderResponse
import uk.gov.hmrc.acceptance.models.init.{InitRequest, InitRequestTimeoutConfig}
import uk.gov.hmrc.acceptance.pages.{ExampleFrontendHomePage, SelectAccountTypePage, TechnicalErrorPage, TimeoutDialoguePartial}

import java.net.URLEncoder

case class TimeoutSpec() extends BaseSpec {

  Scenario("Timeout Dialogue links to relative link") {
    Given("I am on a Bank Account Verification Frontend page")

    val timeoutRelativePath = "/admin/metrics"
    val journeyBuilderData: JourneyBuilderResponse = initializeJourney(
      InitRequest(
        timeoutConfig = Some(InitRequestTimeoutConfig(timeoutRelativePath, 120))
      ).asJsonString())

    startGGJourney(journeyBuilderData)
    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a timeout dialogue appears after a period of inactivity")

    assertThat(TimeoutDialoguePartial().isVisible).isTrue

    Then("I click on sign out and I'm sent to a relative URL correctly")

    TimeoutDialoguePartial().clickSignOut()
    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.getHost("bank-account-verification")}$timeoutRelativePath")
  }

  Scenario("Timeout Dialogue does not link to an absolute URL") {
    Given("I am on a Bank Account Verification Frontend page")

    val timeoutURL = "http://www.google.co.uk"
    val journeyBuilderData: JourneyBuilderResponse = initializeJourney(
      InitRequest(
        timeoutConfig = Some(InitRequestTimeoutConfig(timeoutURL, 120))
      ).asJsonString())

    val session = startGGJourney(journeyBuilderData)
    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a timeout dialogue appears after a period of inactivity")

    assertThat(TimeoutDialoguePartial().isVisible).isTrue

    Then("I click on sign out and I'm sent to a relative URL correctly")

    TimeoutDialoguePartial().clickSignOut()
    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification")}/destroySession?journeyId=${session.journeyId}&timeoutUrl=${URLEncoder.encode(timeoutURL, "UTF-8")}")
    assertThat(TechnicalErrorPage().isOnPage).isTrue
  }

  Scenario("Timeout Dialogue links to an absolute URL on allow list") {
    Given("I am on a Bank Account Verification Frontend page")

    val timeoutURL = TestConfig.url("bank-account-verification-frontend-example")
    val journeyBuilderData: JourneyBuilderResponse = initializeJourney(
      InitRequest(
        timeoutConfig = Some(InitRequestTimeoutConfig(timeoutURL, 120))
      ).asJsonString())

    startGGJourney(journeyBuilderData)
    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a timeout dialogue appears after a period of inactivity")

    assertThat(TimeoutDialoguePartial().isVisible).isTrue

    Then("I click on sign out and I'm sent to the absolute URL that is on the allow list correctly")

    TimeoutDialoguePartial().clickSignOut()
    assertThat(webDriver.getCurrentUrl).isEqualTo(timeoutURL)
    assertThat(ExampleFrontendHomePage().isOnPage).isTrue
  }
}
