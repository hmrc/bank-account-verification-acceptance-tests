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
import uk.gov.hmrc.ui.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.ui.models.init.{InitBACSRequirements, InitRequest}
import uk.gov.hmrc.ui.models.response.v3.CompleteResponse
import uk.gov.hmrc.ui.models.{Account, Individual, JourneyBuilderResponse}
import uk.gov.hmrc.ui.pages.bavfe.{ConfirmDetailsPage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.ui.pages.stubbed.JourneyCompletePage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MockServer

import java.util.UUID

class CheckPersonalAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_NAME: Individual                  =
    Individual(title = Some("Mr"), firstName = Some("Paddy"), lastName = Some("O'Conner-Smith"))
  val DEFAULT_BUILDING_SOCIETY_DETAILS: Account =
    Account("07-00-93", "33333334", Some("NW/1356"), Some("NATIONWIDE BUILDING SOCIETY"))
  val DEFAULT_BANK_ACCOUNT_DETAILS: Account     = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val HMRC_ACCOUNT_DETAILS: Account             = Account("08 32 10", "12001039")

  Scenario("Personal Bank Account Verification successful building society check") {
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

    Given("I want to collect and validate a customers bank account details")

    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='${DEFAULT_NAME.asEscapedString()}'" +
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

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.rollNumber)
    assertThat(actual.personal.get.accountNumberIsWellFormatted).isEqualTo("indeterminate")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_BUILDING_SOCIETY_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("yes")
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

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString() + UUID.randomUUID().toString)
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
              "@.auditType=='TxSucceeded' " +
              "&& @.detail.userAgent=='bank-account-verification-frontend'" +
              s"&& @.detail.callingClient=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Personal Bank Account Verification successful bank check") {
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

    Given("I want to collect and validate a customers bank account details")

    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='${DEFAULT_NAME.asEscapedString()}'" +
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

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(None)
    assertThat(actual.personal.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Personal Bank Account Verification partial name match") {
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
                       |    "name": "${DEFAULT_NAME.asString()}"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a customers bank account details")

    val almostCorrectName = "Patrick O'Conner-Smith"
    val journeyData       = journeyBuilder.initializeJourneyV3()
    val session           = startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(almostCorrectName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='${almostCorrectName.replaceAll("\'", "\\\\\'")}'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    Then("the customer is redirected to continue URL")
    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo(almostCorrectName)
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(None)
    assertThat(actual.personal.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("partial")
    assertThat(actual.personal.get.matchedAccountName.get).isEqualTo(DEFAULT_NAME.asString())
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Personal Bank Account Verification closed bank account") {
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

    Given("I want to collect and validate a customers bank account details")

    val customerName = "Account Closed"
    startGGJourney(journeyBuilder.initializeJourneyV3())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(customerName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error message is displayed to the customer telling them that the account is invalid")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='$customerName'" +
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

  Scenario("Personal Bank Account Verification when the supplied account is a valid business account it is rejected") {
    val accountName: Individual =
      Individual(title = Some("Mr"), firstName = Some(UUID.randomUUID().toString), lastName = Some("Haywood-Smith"))

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
                       |    "code": "BUSINESS_ACCOUNT_NAME_MATCHED"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate personal bank account details")

    val journeyData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV3()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              "&& @.detail.input=='Request to /api/v3/init'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    val session = startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s" && @.detail.input=='Request to ${session.startUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/personal/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a user enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(accountName.asString())
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error message is displayed to the customer telling them that the account is invalid")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='${accountName.asString()}'" +
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

  Scenario("Personal Bank Account Verification unable to find bank account") {
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

    Given("I want to collect and validate a customers bank account details")

    val companyName = "Cannot Match"
    val journeyData = journeyBuilder.initializeJourneyV3()
    val session     = startGGJourney(journeyData)

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='$companyName'" +
              s"&& @.detail.sortCode=='${DEFAULT_BANK_ACCOUNT_DETAILS.sortCode}'" +
              s"&& @.detail.accountNumber=='${DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber}'" +
              "&& @.detail.rollNumber==''" +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER'" +
              ")]"
          )
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

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo("Cannot Match")
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(None)
    assertThat(actual.personal.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_BANK_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Personal Bank Account Verification trying to use HMRC bank account") {
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

    Given("I want to collect and validate a customers bank account details")

    val companyName = "Cannot Match"
    startGGJourney(journeyBuilder.initializeJourneyV3())

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='$companyName'" +
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

  Scenario("Reproduce bank name defect") {
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

    Given("I want to collect and validate a customers bank account details")

    startGGJourney(journeyBuilder.initializeJourneyV3())

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
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='${DEFAULT_NAME.asEscapedString()}'" +
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

  Scenario("Personal Bank Account Verification accounts that don't support Direct Credit are Blocked") {
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

    Given("I want to collect and validate a customers bank account details")

    val companyName = "Cannot Match"
    startGGJourney(
      journeyBuilder.initializeJourneyV3(
        InitRequest(
          bacsRequirements = Some(InitBACSRequirements(directDebitRequired = false, directCreditRequired = true))
        ).asJsonString()
      )
    )

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters HMRC bank account information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error is displayed")

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
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
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='$companyName'" +
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

  Scenario("Personal Bank Account Verification accounts that don't support Direct Debit are Blocked") {
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

    Given("I want to collect and validate a customers bank account details")

    val companyName = "Cannot Match"
    startGGJourney(
      journeyBuilder.initializeJourneyV3(
        InitRequest(
          bacsRequirements = Some(InitBACSRequirements(directDebitRequired = true, directCreditRequired = false))
        ).asJsonString()
      )
    )

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue

    When("a customer enters HMRC bank account information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(companyName)
      .enterSortCode(DEFAULT_BANK_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_BANK_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error is displayed")

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
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
              "&& @.detail.accountType=='personal'" +
              s"&& @.detail.accountName=='$companyName'" +
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
