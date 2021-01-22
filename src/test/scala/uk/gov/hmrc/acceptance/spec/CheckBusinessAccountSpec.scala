package uk.gov.hmrc.acceptance.spec

import java.util.UUID.randomUUID

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.acceptance.models.{Account, InitResponse}
import uk.gov.hmrc.acceptance.pages._
import uk.gov.hmrc.acceptance.utils.MockServer

class CheckBusinessAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_COMPANY_NAME = "P@cking & $orting"
  val DEFAULT_BUILDING_SOCIETY_DETAILS: Account = Account("07-00-93", "33333334", Some("NW/1356"), Some("Lloyds"))
  val DEFAULT_BANK_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val HMRC_ACCOUNT_DETAILS: Account = Account("08 32 10", "12001039")

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

    val session = startJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_COMPANY_NAME)
      .enterSortCode(DEFAULT_BUILDING_SOCIETY_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
      .enterRollNumber(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='business'" +
            s"&& @.detail.companyName=='$DEFAULT_COMPANY_NAME'" +
            s"&& @.detail.sortCode=='${DEFAULT_BUILDING_SOCIETY_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber}'" +
            s"&& @.detail.rollNumber=='${DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get}'" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/${session.journeyId}")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("business")
    assertThat(ExampleFrontendDonePage().getCompanyName).isEqualTo(DEFAULT_COMPANY_NAME)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.storedSortCode())
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get)
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getCompanyNameMatches).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("inapplicable")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("NATIONWIDE BUILDING SOCIETY")
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

    val session = startJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_COMPANY_NAME)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='business'" +
            s"&& @.detail.companyName=='$DEFAULT_COMPANY_NAME'" +
            s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/${session.journeyId}")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("business")
    assertThat(ExampleFrontendDonePage().getCompanyName).isEqualTo(DEFAULT_COMPANY_NAME)
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyNameMatches).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("inapplicable")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("Lloyds")
  }

  Scenario("Business Bank Account Verification closed bank account") {
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

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Account Closed"
    startJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error message is displayed to the company representative telling them that the account is invalid")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='business'" +
            s"&& @.detail.companyName=='$companyName'" +
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

  Scenario("Business Bank Account Verification unable to find bank account") {
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
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "text/plain")
        .withBody(s"""{"requestId":"${randomUUID().toString}","result":"none","isActive":true,"confidence":{}}""".stripMargin)
        .withStatusCode(200)
    )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Cannot Match"
    val session = startJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to the confirm account screen")

    assertThat(ConfirmDetailsPage().isOnPage).isTrue
    assertThat(ConfirmDetailsPage().getAccountType).isEqualTo("Business bank account")
    assertThat(ConfirmDetailsPage().getCompanyName).isEqualTo("Cannot Match")
    assertThat(ConfirmDetailsPage().getSortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(ConfirmDetailsPage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)

    ConfirmDetailsPage().clickContinue()

    Then("the company representative is redirected to the continue URL")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='business'" +
            s"&& @.detail.companyName=='$companyName'" +
            s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/${session.journeyId}")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("business")
    assertThat(ExampleFrontendDonePage().getCompanyName).isEqualTo("Cannot Match")
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getCompanyNameMatches).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getCompanyPostcodeMatches).isEqualTo("inapplicable")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("Lloyds")
  }

  Scenario("Business Bank Account Verification trying to use HMRC bank account") {
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
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "text/plain")
        .withBody(s"""{"requestId":"${randomUUID().toString}","result":"none","isActive":true,"confidence":{}}""".stripMargin)
        .withStatusCode(200)
    )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Cannot Match"
    startJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters HMRC bank account information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(companyName)
      .enterSortCode(HMRC_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(HMRC_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error message is displayed to the user")

    assertThat(BusinessAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='AccountDetailsEntered' " +
            "&& @.detail.accountType=='business'" +
            s"&& @.detail.companyName=='$companyName'" +
            s"&& @.detail.sortCode=='${HMRC_ACCOUNT_DETAILS.sortCode}'" +
            s"&& @.detail.accountNumber=='${HMRC_ACCOUNT_DETAILS.accountNumber}'" +
            "&& @.detail.rollNumber==''" +
            s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )
  }

}
