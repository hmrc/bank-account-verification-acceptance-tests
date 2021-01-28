package uk.gov.hmrc.acceptance.utils

import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.{InitResponse, JourneyBuilderResponse}
import uk.gov.hmrc.acceptance.pages.{GGAuthStubPage, StrideAuthStubPage}

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
      .enterRedirectUrl(s"${TestConfig.getHost("bank-account-verification")}${journeyStart.initResponse.detailsUrl.get}")
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
