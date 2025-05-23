/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ui.specs.v3

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.ui.models.Account
import uk.gov.hmrc.ui.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.ui.models.init.{InitBACSRequirements, InitRequest}
import uk.gov.hmrc.ui.models.response.v3.CompleteResponse
import uk.gov.hmrc.ui.pages.bavfe.{BusinessAccountEntryPage, ConfirmDetailsPage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.ui.pages.stubbed.JourneyCompletePage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MockServer

import java.util.UUID

class CheckBusinessAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_COMPANY_NAME                      = "P@cking & $orting"
  val DEFAULT_BUILDING_SOCIETY_DETAILS: Account =
    Account("07-00-93", "33333334", Some("NW/1356"), Some("NATIONWIDE BUILDING SOCIETY"))
  val DEFAULT_BANK_ACCOUNT_DETAILS: Account     = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val HMRC_ACCOUNT_DETAILS: Account             = Account("08 32 10", "12001039")

  Scenario("Business Bank Account Verification successful building society check") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "MATCHED"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$DEFAULT_COMPANY_NAME'" +
              s"&& @.detail.sortCode=='${DEFAULT_BUILDING_SOCIETY_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber}'" +
              s"&& @.detail.rollNumber=='${DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get}'" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(DEFAULT_COMPANY_NAME)
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber.get).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("indeterminate")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("yes")
  }

  Scenario("Check that correct user agent and true calling client is passed through to BARS") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "MATCHED"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to audit where a request came from")

    startGGJourney(journeyBuilder.initializeJourneyV3())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_COMPANY_NAME + UUID.randomUUID().toString)
      .enterSortCode(DEFAULT_BUILDING_SOCIETY_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
      .enterRollNumber(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber.get)
      .clickContinue()

    Then("the user agent and true calling client is sent over to BARS and correctly audited")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='businessBankAccountCheck' " +
              "&& @.detail.userAgent=='bank-account-verification-frontend'" +
              s"&& @.detail.callingClient=='$DEFAULT_SERVICE_IDENTIFIER'" +
              "&& @.detail.context=='modulr_business_fromapi'" +
              "&& @.detail.request.length()==2" +
              "&& @.detail.response.length()==8" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account Verification successful bank check") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "MATCHED"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$DEFAULT_COMPANY_NAME'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(DEFAULT_COMPANY_NAME)
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Business Bank Account Verification partial name match") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "CLOSE_MATCH",
                       |    "name": "$DEFAULT_COMPANY_NAME"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(s"$DEFAULT_COMPANY_NAME LTD")
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$DEFAULT_COMPANY_NAME LTD'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(s"$DEFAULT_COMPANY_NAME LTD")
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("partial")
    assertThat(actual.business.get.matchedAccountName.get).isEqualTo(s"$DEFAULT_COMPANY_NAME")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Business Bank Account Verification closed bank account") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "ACCOUNT_DOES_NOT_EXIST"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Account Closed"
    startGGJourney(journeyBuilder.initializeJourneyV3())

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$companyName'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("accountNumber")
    assertThatInputFieldErrorMessageExists("accountNumber")
  }

  Scenario("Business Bank Account Verification unable to find bank account") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "WRONG_PARTICIPANT"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Cannot Match"
    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$companyName'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo("Cannot Match")
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("indeterminate")
    assertThat(actual.business.get.accountExists.get).isEqualTo("indeterminate")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Business Bank Account Verification trying to use HMRC bank account") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "WRONG_PARTICIPANT"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Cannot Match"
    startGGJourney(journeyBuilder.initializeJourneyV3())

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$companyName'" +
              s"&& @.detail.sortCode=='${HMRC_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${HMRC_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account Verification accounts that don't support Direct Credit are Blocked") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "WRONG_PARTICIPANT"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Cannot Match"
    startGGJourney(
      journeyBuilder.initializeJourneyV3(
        InitRequest(
          bacsRequirements = Some(InitBACSRequirements(directDebitRequired = false, directCreditRequired = true))
        ).asJsonString()
      )
    )

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters HMRC bank account information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error message is displayed to the user")

    assertThat(BusinessAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$companyName'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account Verification accounts that don't support Direct Debit are Blocked") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(MODULR_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""
                       |{
                       |  "id": "C12001569Z",
                       |  "result": {
                       |    "code": "WRONG_PARTICIPANT"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val companyName = "Cannot Match"
    startGGJourney(
      journeyBuilder.initializeJourneyV3(
        InitRequest(
          bacsRequirements = Some(InitBACSRequirements(directDebitRequired = true, directCreditRequired = false))
        ).asJsonString()
      )
    )

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters HMRC bank account information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error message is displayed to the user")

    assertThat(BusinessAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business'" +
              s"&& @.detail.companyName=='$companyName'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }
}
