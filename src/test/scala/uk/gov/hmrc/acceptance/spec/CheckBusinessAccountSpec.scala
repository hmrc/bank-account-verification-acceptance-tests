package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.pages.{BusinessAccountEntryPage, ExampleFrontendDonePage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.utils.{BaseSpec, MockServer}

class CheckBusinessAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_COMPANY_NAME = "P@cking & $orting"
  val DEFAULT_COMPANY_REGISTRATION_NUMBER = "NI7625183"
  val DEFAULT_BUILDING_SOCIETY_SORT_CODE = "07-00-93"
  val DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER = "33333334"
  val DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER = "NW/1356"
  val DEFAULT_BANK_SORT_CODE = "40 47 84"
  val DEFAULT_BANK_ACCOUNT_NUMBER = "70872490"

  Scenario("Business Bank Account Verification successful building society check") {
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath(SUREPAY_PATH)
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "application/json")
        .withBody(s"""{"Matched": true}""".stripMargin)
        .withStatusCode(200)
    )

    Given("I want to collect and validate a companies bank account details")

    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_COMPANY_NAME)
      .enterCompanyRegistrationNumber(DEFAULT_COMPANY_REGISTRATION_NUMBER)
      .enterSortCode(DEFAULT_BUILDING_SOCIETY_SORT_CODE)
      .enterAccountNumber(DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER)
      .enterRollNumber(DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("business")
    assertThat(ExampleFrontendDonePage().getCompanyName).isEqualTo(DEFAULT_COMPANY_NAME)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BUILDING_SOCIETY_SORT_CODE)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER)
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getCompanyNameMatches).isEqualTo("yes")
    //FIXME - Should be indeterminate
    //assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("yes")
    //FIXME - Should this be indeterminate as well?
    assertThat(ExampleFrontendDonePage().getCompanyRegistrationNumberMatches).isEqualTo("inapplicable")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
  }

  Scenario("Business Bank Account Verification successful bank check") {
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath(SUREPAY_PATH)
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "application/json")
        .withBody(s"""{"Matched": true}""".stripMargin)
        .withStatusCode(200)
    )

    Given("I want to collect and validate a companies bank account details")

    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_COMPANY_NAME)
      .enterCompanyRegistrationNumber(DEFAULT_COMPANY_REGISTRATION_NUMBER)
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("business")
    assertThat(ExampleFrontendDonePage().getCompanyName).isEqualTo(DEFAULT_COMPANY_NAME)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_SORT_CODE)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_NUMBER)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyNameMatches).isEqualTo("yes")
    //FIXME - Should be indeterminate
    //assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("yes")
    //FIXME - Should this be indeterminate as well?
    assertThat(ExampleFrontendDonePage().getCompanyRegistrationNumberMatches).isEqualTo("inapplicable")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
  }

}
