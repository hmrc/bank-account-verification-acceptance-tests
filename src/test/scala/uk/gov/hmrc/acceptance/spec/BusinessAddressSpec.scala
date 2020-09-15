package uk.gov.hmrc.acceptance.spec

import java.util.UUID.randomUUID

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.pages.{BusinessAccountEntryPage, ExampleFrontendDonePage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.stubs.creditsafe.CreditSafePayload
import uk.gov.hmrc.acceptance.utils._
import uk.gov.hmrc.acceptance.utils.types.{Account, Address, Business, InitJourney}

class BusinessAddressSpec extends BaseSpec with MockServer {

  val DEFAULT_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490")
  val DEFAULT_BUSINESS_ADDRESS: Option[Address] = Some(Address(List("22303 Darwin Turnpike"), postcode = Some("CZ0 8IW")))
  val DEFAULT_BUSINESS: Business = Business("P@cking & $orting Creditsafe", "NI7625183", DEFAULT_BUSINESS_ADDRESS)

  Scenario("Business Bank Account Verification with address is successful") {
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath(SUREPAY_PATH)
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "application/json")
        .withBody(s"""{"Matched": false, "ReasonCode": "SCNS"}""".stripMargin)
        .withStatusCode(200)
    )
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath(CREDITSAFE_PATH)
        .withBody(CreditSafePayload(
          DEFAULT_ACCOUNT_DETAILS.sortCode,
          DEFAULT_ACCOUNT_DETAILS.accountNumber,
          DEFAULT_BUSINESS.companyName,
          DEFAULT_BUSINESS.address.get.postcode.get,
          DEFAULT_BUSINESS.registrationNumber
        ).asJsonString())
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "text/plain")
        .withBody(s"""{"requestId":"${randomUUID().toString}","result":"exact","isActive":true,"confidence":{}}""")
        .withStatusCode(200)
    )

    Given("I want to collect and validate a companies bank account details")

    val journeyId: String = initializeJourney(InitJourney(address = DEFAULT_BUSINESS_ADDRESS).asJsonString())
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterCompanyRegistrationNumber(DEFAULT_BUSINESS.registrationNumber)
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("business")
    assertThat(ExampleFrontendDonePage().getCompanyName).isEqualTo(DEFAULT_BUSINESS.companyName)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.sortCode)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getAddress).isEqualTo(DEFAULT_BUSINESS_ADDRESS.get.asStringWithCR())
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyNameMatches).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyRegistrationNumberMatches).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")

    mockServer.verify(HttpRequest.request().withPath(CREDITSAFE_PATH), VerificationTimes.atLeast(1))
  }
}
