package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.acceptance.models.{Account, Individual}
import uk.gov.hmrc.acceptance.pages.{ConfirmDetailsPage, ExampleFrontendDonePage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.stubs.transunion.CallValidateResponseBuilder
import uk.gov.hmrc.acceptance.utils.MockServer

class CheckPersonalAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_NAME: Individual = Individual(title = Some("Mr"), firstName = Some("Paddy"), lastName = Some("O'Conner-Smith"))
  val DEFAULT_BUILDING_SOCIETY_DETAILS: Account = Account("07-00-93", "33333334", Some("NW/1356"), Some("Lloyds"))
  val DEFAULT_BANK_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val HMRC_ACCOUNT_DETAILS: Account = Account("08 32 10", "12001039")

  Scenario("Personal Bank Account Verification successful building society check") {
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

    val session = startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_BUILDING_SOCIETY_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
      .enterRollNumber(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get)
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
            s"&& @.detail.sortCode=='${DEFAULT_BUILDING_SOCIETY_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber}'" +
            s"&& @.detail.rollNumber=='${DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get}'" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/${session.journeyId}")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.storedSortCode())
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get)
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

    val session = startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
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
            s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/${session.journeyId}")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
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
    startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
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
            s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
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
    val session = startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
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
            s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(ConfirmDetailsPage().isOnPage).isTrue
    assertThat(ConfirmDetailsPage().getAccountType).isEqualTo("Personal bank account")
    assertThat(ConfirmDetailsPage().getAccountName).isEqualTo("Cannot Match")
    assertThat(ConfirmDetailsPage().getSortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(ConfirmDetailsPage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)

    ConfirmDetailsPage().clickContinue()

    Then("the customer is redirected to continue URL")

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/${session.journeyId}")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo("Cannot Match")
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
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
    startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters HMRC bank account information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(HMRC_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(HMRC_ACCOUNT_DETAILS.accountNumber)
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
            s"&& @.detail.sortCode=='${HMRC_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${HMRC_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Reproduce bank name defect") {
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

    startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
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
            s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

  }
}
