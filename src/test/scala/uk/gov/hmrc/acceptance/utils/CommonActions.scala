package uk.gov.hmrc.acceptance.utils

import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.{InitResponse, JourneyBuilderResponse}
import uk.gov.hmrc.acceptance.pages.{AuthStubPage, SelectAccountTypePage}

trait CommonActions extends BrowserDriver {

  def startJourney(journeyStart: JourneyBuilderResponse): InitResponse = {
    go to s"${TestConfig.url("auth-login-stub")}/gg-sign-in"
    AuthStubPage()
      .enterCredID(journeyStart.credId)
      .enterRedirectUrl(s"${TestConfig.getHost("bank-account-verification")}${journeyStart.initResponse.startUrl}")
      .submit()
    journeyStart.initResponse
  }

  def continueJourney(journeyStart: JourneyBuilderResponse): Unit = {
    go to s"${TestConfig.url("auth-login-stub")}/gg-sign-in"
    AuthStubPage()
      .enterCredID(journeyStart.credId)
      .enterRedirectUrl(s"${TestConfig.getHost("bank-account-verification")}${journeyStart.initResponse.detailsUrl.get}")
      .submit()
  }

}
