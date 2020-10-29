package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.Individual
import uk.gov.hmrc.acceptance.models.InitJourney.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.acceptance.pages.{ConfirmDetailsPage, ExampleFrontendDonePage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.stubs.transunion.CallValidateResponseBuilder
import uk.gov.hmrc.acceptance.tags.Zap
import uk.gov.hmrc.acceptance.utils.MockServer

class CheckPersonalAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_NAME: Individual = Individual(title = Some("Mr"), firstName = Some("Paddy"), lastName = Some("O'Conner-Smith"))
  val DEFAULT_BUILDING_SOCIETY_SORT_CODE = "07-00-93"
  val DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER = "33333334"
  val DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER = "NW/1356"
  val DEFAULT_BANK_SORT_CODE = "40 47 84"
  val DEFAULT_BANK_ACCOUNT_NUMBER = "70872490"
  val HMRC_SORT_CODE = "08 32 10"
  val HMRC_BANK_ACCOUNT_NUMBER ="12001039"

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

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_BUILDING_SOCIETY_SORT_CODE)
      .enterAccountNumber(DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER)
      .enterRollNumber(DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER)
      .clickContinue()

    Then("the customer is redirected to continue URL")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='personal'" +
            s"&& @.detail.accountName=='${DEFAULT_NAME.asEscapedString()}'" +
            s"&& @.detail.sortCode=='$DEFAULT_BUILDING_SOCIETY_SORT_CODE'" +
            s"&& @.detail.accountNumber=='$DEFAULT_BUILDING_SOCIETY_ACCOUNT_NUMBER'" +
            s"&& @.detail.rollNumber=='$DEFAULT_BUILDING_SOCIETY_ROLL_NUMBER'" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME.asString())
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

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("the customer is redirected to continue URL")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='personal'" +
            s"&& @.detail.accountName=='${DEFAULT_NAME.asEscapedString()}'" +
            s"&& @.detail.sortCode=='$DEFAULT_BANK_SORT_CODE'" +
            s"&& @.detail.accountNumber=='$DEFAULT_BANK_ACCOUNT_NUMBER'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME.asString())
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

    val companyName = "Account Closed"
    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("an error message is displayed to the customer telling them that the account is invalid")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='personal'" +
            s"&& @.detail.accountName=='$companyName'" +
            s"&& @.detail.sortCode=='$DEFAULT_BANK_SORT_CODE'" +
            s"&& @.detail.accountNumber=='$DEFAULT_BANK_ACCOUNT_NUMBER'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

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

    val companyName = "Cannot Match"
    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(DEFAULT_BANK_SORT_CODE)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("the customer is redirected to the confirm account screen")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='personal'" +
            s"&& @.detail.accountName=='$companyName'" +
            s"&& @.detail.sortCode=='$DEFAULT_BANK_SORT_CODE'" +
            s"&& @.detail.accountNumber=='$DEFAULT_BANK_ACCOUNT_NUMBER'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

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

  Scenario("Personal Bank Account Verification trying to use HMRC bank account") {
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

    val companyName = "Cannot Match"
    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters HMRC bank account information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(HMRC_SORT_CODE)
      .enterAccountNumber(HMRC_BANK_ACCOUNT_NUMBER)
      .clickContinue()

    Then("an error is displayed")

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='personal'" +
            s"&& @.detail.accountName=='$companyName'" +
            s"&& @.detail.sortCode=='$HMRC_SORT_CODE'" +
            s"&& @.detail.accountNumber=='$HMRC_BANK_ACCOUNT_NUMBER'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )
  }

}
