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
import uk.gov.hmrc.ui.config.TestConfig
import uk.gov.hmrc.ui.models._
import uk.gov.hmrc.ui.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.ui.models.init.{InitBACSRequirements, InitRequest, MaxCallConfig, PrepopulatedData}
import uk.gov.hmrc.ui.models.response.v3.CompleteResponse
import uk.gov.hmrc.ui.pages.bavfe.{BusinessAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.ui.pages.stubbed.{JourneyCompletePage, TooManyAttemptsPage}
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils._

import java.util.UUID

class BusinessAddressSpec extends BaseSpec with MockServer {

  // **NOTE TO FUTURE TESTERS** Remember caching is based on a combination of name/sort code/account number.
  // When adding new scenarios make sure you generate a unique name, if you use BUSINESS_NAME you will probably get a cached response!

  val DEFAULT_ACCOUNT_DETAILS: Account          = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val ALTERNATE_ACCOUNT_DETAILS: Account        = Account("207102", "80044660", bankName = Some("BARCLAYS BANK PLC"))
  val ACCOUNT_NUMBER_WITH_IBAN: Account         =
    Account("601613", "26344696", bankName = Some("HSBC UK BANK PLC"), iban = Some("GB36 HBUK 6016 1326 3446 96"))
  val UNKNOWN_ACCOUNT_DETAILS: Account          = Account("207106", "80044666")
  val DEFAULT_BUSINESS_ADDRESS: Option[Address] = Some(
    Address(List("22303 Darwin Turnpike"), postcode = Some("CZ0 8IW"))
  )
  val BUSINESS_NAME: String                     = UUID.randomUUID().toString
  val DEFAULT_BUSINESS: Business                = Business(BUSINESS_NAME, DEFAULT_BUSINESS_ADDRESS)

  Scenario("Business Bank Account Verification with address is successful") {
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

    val journeyData: JourneyBuilderResponse =
      journeyBuilder.initializeJourneyV3(InitRequest(address = DEFAULT_BUSINESS_ADDRESS).asJsonString())

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

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(DEFAULT_BUSINESS.companyName)
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to ${session.completeUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account Verification is successful with IBAN in response") {
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

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterSortCode(ACCOUNT_NUMBER_WITH_IBAN.sortCode)
      .enterAccountNumber(ACCOUNT_NUMBER_WITH_IBAN.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(DEFAULT_BUSINESS.companyName)
    assertThat(actual.business.get.sortCode).isEqualTo(ACCOUNT_NUMBER_WITH_IBAN.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(ACCOUNT_NUMBER_WITH_IBAN.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(ACCOUNT_NUMBER_WITH_IBAN.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("yes")
    assertThat(actual.business.get.iban).isEqualTo(ACCOUNT_NUMBER_WITH_IBAN.iban)

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to ${session.completeUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account change is successful") {
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

    Given("I want to collect and validate business bank account details")

    val journeyBuilderData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV3(
      InitRequest(
        address = DEFAULT_BUSINESS_ADDRESS,
        prepopulatedData = Some(PrepopulatedData(accountType = "business"))
      ).asJsonString()
    )

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

    val session = startGGJourney(journeyBuilderData)

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

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val initial: CompleteResponse =
      journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyBuilderData.credId)

    assertThat(initial.accountType).isEqualTo("business")
    assertThat(initial.business.get.companyName).isEqualTo(DEFAULT_BUSINESS.companyName)
    assertThat(initial.business.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(initial.business.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(initial.business.get.rollNumber).isEqualTo(None)
    assertThat(initial.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(initial.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(initial.business.get.accountExists.get).isEqualTo("yes")
    assertThat(initial.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(initial.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(initial.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to ${session.completeUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("the user goes back to the details page and changes the bank account details")

    continueGGJourney(journeyBuilderData)

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterSortCode(ALTERNATE_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(ALTERNATE_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the updated details have been saved")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val updated = journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyBuilderData.credId)

    assertThat(updated.accountType).isEqualTo("business")
    assertThat(updated.business.get.companyName).isEqualTo(DEFAULT_BUSINESS.companyName)
    assertThat(updated.business.get.sortCode).isEqualTo(ALTERNATE_ACCOUNT_DETAILS.storedSortCode())
    assertThat(updated.business.get.accountNumber).isEqualTo(ALTERNATE_ACCOUNT_DETAILS.accountNumber)
    assertThat(updated.business.get.rollNumber).isEqualTo(None)
    assertThat(updated.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(updated.business.get.accountExists.get).isEqualTo("yes")
    assertThat(updated.business.get.sortCodeBankName.get).isEqualTo(ALTERNATE_ACCOUNT_DETAILS.bankName.get)
    assertThat(updated.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("yes")
    assertThat(updated.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Business Bank Account cannot be changed to an unknown bank") {
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

    Given("I want to collect and validate business bank account details")

    val journeyBuilderData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV3(
      InitRequest(
        address = DEFAULT_BUSINESS_ADDRESS,
        prepopulatedData = Some(PrepopulatedData(accountType = "business"))
      ).asJsonString()
    )

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

    val session = startGGJourney(journeyBuilderData)

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

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse =
      journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyBuilderData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(DEFAULT_BUSINESS.companyName)
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to ${session.completeUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("the user goes back to the details page and changes the bank account details")

    continueGGJourney(journeyBuilderData)

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    BusinessAccountEntryPage()
      .enterCompanyName(DEFAULT_BUSINESS.companyName)
      .enterSortCode(UNKNOWN_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(UNKNOWN_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("an error is displayed")

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='AccountDetailsEntered' " +
              "&& @.detail.accountType=='business' " +
              s"&& @.detail.companyName=='${DEFAULT_BUSINESS.companyName}' " +
              s"&& @.detail.sortCode=='${UNKNOWN_ACCOUNT_DETAILS.sortCode}' " +
              s"&& @.detail.accountNumber=='${UNKNOWN_ACCOUNT_DETAILS.accountNumber}' " +
              "&& @.detail.rollNumber=='' " +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER' " +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(BusinessAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")
  }

  Scenario("Business Bank Account Verification when the supplied name is a close match") {
    val businessDetails: Business = Business(UUID.randomUUID().toString, DEFAULT_BUSINESS_ADDRESS)

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
                       |    "name": "Real company name"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val journeyBuilderData: JourneyBuilderResponse =
      journeyBuilder.initializeJourneyV3(InitRequest(address = DEFAULT_BUSINESS_ADDRESS).asJsonString())

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

    val session = startGGJourney(journeyBuilderData)

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

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(businessDetails.companyName)
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse =
      journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyBuilderData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(businessDetails.companyName)
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("partial")
    assertThat(actual.business.get.matchedAccountName.get).isEqualTo("Real company name")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(HttpRequest.request().withPath(MODULR_PATH), VerificationTimes.atLeast(1))
    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to ${session.completeUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account Verification when the supplied account is a personal account that is a match") {
    val businessDetails: Business = Business(UUID.randomUUID().toString, DEFAULT_BUSINESS_ADDRESS)

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
                       |    "code": "PERSONAL_ACCOUNT_NAME_MATCHED"
                       |    }
                       |}
                       |""".stripMargin)
          .withStatusCode(201)
      )

    Given("I want to collect and validate a companies bank account details")

    val journeyBuilderData: JourneyBuilderResponse =
      journeyBuilder.initializeJourneyV3(InitRequest(address = DEFAULT_BUSINESS_ADDRESS).asJsonString())

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

    val session = startGGJourney(journeyBuilderData)

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

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to /bank-account-verification/verify/business/${session.journeyId}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    When("a company representative enters all required information and clicks continue")

    BusinessAccountEntryPage()
      .enterCompanyName(businessDetails.companyName)
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the company representative is redirected to continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse =
      journeyBuilder.getDataCollectedByBAVFEV3(session.journeyId, journeyBuilderData.credId)

    assertThat(actual.accountType).isEqualTo("business")
    assertThat(actual.business.get.companyName).isEqualTo(businessDetails.companyName)
    assertThat(actual.business.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.business.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.business.get.rollNumber).isEqualTo(None)
    assertThat(actual.business.get.accountNumberIsWellFormatted).isEqualTo("yes")
    assertThat(actual.business.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.business.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.business.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.business.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.business.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(HttpRequest.request().withPath(MODULR_PATH), VerificationTimes.atLeast(1))
    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              s"&& @.detail.input=='Request to ${session.completeUrl}'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }

  Scenario("Business Bank Account accepts only three tries when relevant configuration is set") {
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

    val companyName = "Fail Three Tries"
    val initRequest = InitRequest(
      bacsRequirements = Some(InitBACSRequirements(directDebitRequired = true, directCreditRequired = false)),
      maxCallConfig = Some(
        MaxCallConfig(
          count = 3,
          redirectUrl = s"${TestConfig.environmentHost}:${TestConfig.mockServerPort()}/too/many/attempts"
        )
      )
    ).asJsonString()
    val session     = startGGJourney(journeyBuilder.initializeJourneyV3(initRequest))

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("I enter invalid details three times")

    BusinessAccountEntryPage()
      .enterCompanyName(companyName)
      .enterSortCode(ALTERNATE_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(ALTERNATE_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    BusinessAccountEntryPage()
      .clickContinue()

    BusinessAccountEntryPage()
      .clickContinue()

    Then("I am returned to the calling service using the maxCallCountRedirectUrl")

    assertThat(TooManyAttemptsPage().isOnPage).isTrue
    assertThat(TooManyAttemptsPage().getJourneyId).isEqualTo(session.journeyId)

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              "&& @.detail.input=='Request to /api/v3/init'" +
              s"&& @.detail.requestBody=='$initRequest'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )
  }
}
