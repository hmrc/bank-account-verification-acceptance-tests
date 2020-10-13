package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.pages.{ConfirmDetailsPage, ExampleFrontendDonePage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.stubs.transunion.CallValidateResponseBuilder
import uk.gov.hmrc.acceptance.tags.Zap
import uk.gov.hmrc.acceptance.utils.{BaseSpec, MockServer}

class CheckPersonalAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_NAME = "Patrick O'Conner-Smith"
  val DEFAULT_BUILDING_SOCIETY_SORT_CODE = "07-00-93"
  val DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER = "33333334"
  val DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER = "NW/1356"
  val DEFAULT_BANK_SORT_CODE = "40 47 84"
  val DEFAULT_BANK_ACCOUNT_NUMBER = "70872490"

  Scenario("Personal Bank Account Verification successful building society check", Zap) {
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

    Given("I want to collect and validate a customers bank account details")

    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME)
      .enterSortCode(DEFAULT_BUILDING_SOCIETY_SORT_CODE)
      .enterAccountNumber(DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER)
      .enterRollNumber(DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER)
      .clickContinue()

    Then("the customer is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BUILDING_SOCIETY_SORT_CODE)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER)
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountNameMatched).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountNonConsented).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getAccountOwnerDeceased).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("NATIONWIDE BUILDING SOCIETY")
  }

  Scenario("Personal Bank Account Verification successful bank check") {
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

    Given("I want to collect and validate a customers bank account details")

    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME)
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("the customer is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_SORT_CODE)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_NUMBER)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountNameMatched).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountNonConsented).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getAccountOwnerDeceased).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("Lloyds")
  }

  Scenario("Personal Bank Account Verification closed bank account") {
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath(SUREPAY_PATH)
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "application/json")
        .withBody(s"""{"Matched": false, "ReasonCode": "AC01"}""".stripMargin)
        .withStatusCode(200)
    )

    Given("I want to collect and validate a customers bank account details")

    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName("Account Closed")
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("an error message is displayed to the customer telling them that the account is invalid")

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("accountNumber")
    assertThatInputFieldErrorMessageExists("accountNumber")
  }

  Scenario("Personal Bank Account Verification unable to find bank account") {
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
        .withPath(TRANSUNION_PATH)
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "application/xml")
        .withBody(new CallValidateResponseBuilder()
          .withError("BV3: Unknown account")
          .build())
        .withStatusCode(200)
    )

    Given("I want to collect and validate a customers bank account details")

    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName("Cannot Match")
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("the customer is redirected to the confirm account screen")

    assertThat(ConfirmDetailsPage().isOnPage).isTrue
    assertThat(ConfirmDetailsPage().getAccountName).isEqualTo("Cannot Match")
    assertThat(ConfirmDetailsPage().getSortCode).isEqualTo(DEFAULT_BANK_SORT_CODE)
    assertThat(ConfirmDetailsPage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_NUMBER)

    ConfirmDetailsPage().clickContinue()

    Then("the customer is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo("Cannot Match")
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_SORT_CODE)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_NUMBER)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getAccountNameMatched).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getAccountNonConsented).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getAccountOwnerDeceased).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("Lloyds")
  }

}
