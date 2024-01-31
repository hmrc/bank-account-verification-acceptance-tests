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

package uk.gov.hmrc.ui.specs.v1

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpError, HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.ui.models._
import uk.gov.hmrc.ui.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.ui.models.init.{InitRequest, PrepopulatedData}
import uk.gov.hmrc.ui.models.response.v1.CompleteResponse
import uk.gov.hmrc.ui.pages.bavfe.{PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.ui.pages.stubbed.JourneyCompletePage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.stubs.transunion.{CallValidateResponseBuilder, IdentityCheckBuilder}
import uk.gov.hmrc.ui.utils._

import java.util.UUID

class PersonalAddressSpec extends BaseSpec with MockServer {

  // **NOTE TO FUTURE TESTERS** Remember caching is based on a combination of name/sort code/account number.
  // When adding new scenarios make sure you generate a unique name, if you use FIRST_NAME you will probably get a cached response!

  val FIRST_NAME: String                 = UUID.randomUUID().toString
  val DEFAULT_NAME: Individual           =
    Individual(title = Some("Mr"), firstName = Some(FIRST_NAME), lastName = Some("O'Conner-Smith"))
  val DEFAULT_ACCOUNT_DETAILS: Account   = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val ALTERNATE_ACCOUNT_DETAILS: Account = Account("207102", "80044660", bankName = Some("BARCLAYS BANK PLC"))
  val UNKNOWN_ACCOUNT_DETAILS: Account   = Account("207106", "80044666")
  val DEFAULT_ADDRESS: Address           = Address(List("2664 Little Darwin"), postcode = Some("CZ0 9AV"))

  Scenario("Personal Bank Account Verification with address is successful") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(SUREPAY_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""{"Matched": false, "ReasonCode": "SCNS"}""".stripMargin)
          .withStatusCode(200)
      )
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(TRANSUNION_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/xml")
          .withBody(
            new CallValidateResponseBuilder()
              .setInputIndividualData(DEFAULT_NAME)
              .setInputAddress(DEFAULT_ADDRESS)
              .identityCheck(
                new IdentityCheckBuilder()
                  .nameMatched(DEFAULT_NAME.asString())
                  .currentAddressMatched(DEFAULT_ADDRESS)
                  .build()
              )
              .build()
          )
          .withStatusCode(200)
      )

    Given("I want to collect and validate personal bank account details")

    val journeyData: JourneyBuilderResponse =
      journeyBuilder.initializeJourneyV1(InitRequest(address = Some(DEFAULT_ADDRESS)).asJsonString())

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              "&& @.detail.input=='Request to /api/init'" +
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
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the user is redirected to the continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV1(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(None)
    assertThat(actual.personal.get.address.get).isEqualTo(DEFAULT_ADDRESS)
    assertThat(actual.personal.get.accountNumberWithSortCodeIsValid).isEqualTo("yes")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.personal.get.addressMatches.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.nonConsented.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.subjectHasDeceased.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(HttpRequest.request().withPath(TRANSUNION_PATH), VerificationTimes.atLeast(1))
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

  Scenario("Personal Bank Account change is successful") {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(SUREPAY_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""{"Matched": false, "ReasonCode": "SCNS"}""".stripMargin)
          .withStatusCode(200)
      )
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(TRANSUNION_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/xml")
          .withBody(
            new CallValidateResponseBuilder()
              .setInputIndividualData(DEFAULT_NAME)
              .setInputAddress(DEFAULT_ADDRESS)
              .identityCheck(
                new IdentityCheckBuilder()
                  .nameMatched(DEFAULT_NAME.asString())
                  .currentAddressMatched(DEFAULT_ADDRESS)
                  .build()
              )
              .build()
          )
          .withStatusCode(200)
      )

    Given("I want to collect and validate personal bank account details")

    val journeyData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV1(
      InitRequest(address = Some(DEFAULT_ADDRESS), prepopulatedData = Some(PrepopulatedData(accountType = "personal")))
        .asJsonString()
    )

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              "&& @.detail.input=='Request to /api/init'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    val session = startGGJourney(journeyData)

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a user enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the user is redirected to the continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val initial: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV1(session.journeyId, journeyData.credId)

    assertThat(initial.accountType).isEqualTo("personal")
    assertThat(initial.personal.get.accountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(initial.personal.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(initial.personal.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(initial.personal.get.rollNumber).isEqualTo(None)
    assertThat(initial.personal.get.address.get).isEqualTo(DEFAULT_ADDRESS)
    assertThat(initial.personal.get.accountNumberWithSortCodeIsValid).isEqualTo("yes")
    assertThat(initial.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(initial.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(initial.personal.get.addressMatches.get).isEqualTo("indeterminate")
    assertThat(initial.personal.get.nonConsented.get).isEqualTo("indeterminate")
    assertThat(initial.personal.get.subjectHasDeceased.get).isEqualTo("indeterminate")
    assertThat(initial.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(initial.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(initial.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

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

    continueGGJourney(journeyData)

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

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
      .enterSortCode(ALTERNATE_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(ALTERNATE_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the updated details have been saved")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val updated: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV1(session.journeyId, journeyData.credId)

    assertThat(updated.accountType).isEqualTo("personal")
    assertThat(updated.personal.get.accountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(updated.personal.get.sortCode).isEqualTo(ALTERNATE_ACCOUNT_DETAILS.storedSortCode())
    assertThat(updated.personal.get.accountNumber).isEqualTo(ALTERNATE_ACCOUNT_DETAILS.accountNumber)
    assertThat(updated.personal.get.rollNumber).isEqualTo(None)
    assertThat(updated.personal.get.address.get).isEqualTo(DEFAULT_ADDRESS)
    assertThat(updated.personal.get.accountNumberWithSortCodeIsValid).isEqualTo("yes")
    assertThat(updated.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(updated.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(updated.personal.get.addressMatches.get).isEqualTo("indeterminate")
    assertThat(updated.personal.get.nonConsented.get).isEqualTo("indeterminate")
    assertThat(updated.personal.get.subjectHasDeceased.get).isEqualTo("indeterminate")
    assertThat(updated.personal.get.sortCodeBankName.get).isEqualTo(ALTERNATE_ACCOUNT_DETAILS.bankName.get)
    assertThat(updated.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("yes")
    assertThat(updated.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")
  }

  Scenario("Personal Bank Account cannot be changed to an unknown bank") {
    val accountName: Individual = Individual(title = Some("Mr"), firstName = Some(FIRST_NAME), lastName = Some("Jones"))

    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(SUREPAY_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""{"Matched": false, "ReasonCode": "SCNS"}""".stripMargin)
          .withStatusCode(200)
      )
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(TRANSUNION_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/xml")
          .withBody(
            new CallValidateResponseBuilder()
              .setInputIndividualData(accountName)
              .setInputAddress(DEFAULT_ADDRESS)
              .identityCheck(
                new IdentityCheckBuilder()
                  .nameMatched(accountName.asString())
                  .currentAddressMatched(DEFAULT_ADDRESS)
                  .build()
              )
              .build()
          )
          .withStatusCode(200)
      )

    Given("I want to collect and validate personal bank account details")

    val journeyData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV1(
      InitRequest(address = Some(DEFAULT_ADDRESS), prepopulatedData = Some(PrepopulatedData(accountType = "personal")))
        .asJsonString()
    )

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              "&& @.detail.input=='Request to /api/init'" +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    val session = startGGJourney(journeyData)

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a user enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(accountName.asString())
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the user is redirected to the continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV1(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo(accountName.asString())
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(None)
    assertThat(actual.personal.get.address.get).isEqualTo(DEFAULT_ADDRESS)
    assertThat(actual.personal.get.accountNumberWithSortCodeIsValid).isEqualTo("yes")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.personal.get.addressMatches.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.nonConsented.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.subjectHasDeceased.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

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

    When("the user goes back to the details page and changes the bank account details to an unknown bank")

    continueGGJourney(journeyData)

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

    PersonalAccountEntryPage()
      .enterAccountName(accountName.asString())
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
              "&& @.detail.accountType=='personal' " +
              s"&& @.detail.accountName=='${accountName.asString()}' " +
              s"&& @.detail.sortCode=='${UNKNOWN_ACCOUNT_DETAILS.sortCode}' " +
              s"&& @.detail.accountNumber=='${UNKNOWN_ACCOUNT_DETAILS.accountNumber}' " +
              "&& @.detail.rollNumber=='' " +
              s"&& @.detail.trueCallingService=='$DEFAULT_SERVICE_IDENTIFIER' " +
              ")]"
          )
        ),
      VerificationTimes.atLeast(1)
    )

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")
  }

  Scenario("Personal Bank Account Verification when the supplied name is a close match") {
    val accountName: Individual =
      Individual(title = Some("Mr"), firstName = Some(UUID.randomUUID().toString), lastName = Some("O'Conner-Smith"))

    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(SUREPAY_PATH)
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "application/json")
          .withBody(s"""{"Matched": false, "ReasonCode": "MBAM", "Name": "Patrick O'Conner-Smith"}""".stripMargin)
          .withStatusCode(200)
      )

    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath(TRANSUNION_PATH)
      )
      .error(
        HttpError
          .error()
          .withDropConnection(true)
      )

    Given("I want to collect and validate personal bank account details")

    val journeyData: JourneyBuilderResponse =
      journeyBuilder.initializeJourneyV1(InitRequest(address = Some(DEFAULT_ADDRESS)).asJsonString())

    mockServer.verify(
      HttpRequest
        .request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath(
            "$[?(" +
              "@.auditType=='RequestReceived' " +
              "&& @.detail.input=='Request to /api/init'" +
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
      .enterSortCode(DEFAULT_ACCOUNT_DETAILS.sortCode)
      .enterAccountNumber(DEFAULT_ACCOUNT_DETAILS.accountNumber)
      .clickContinue()

    Then("the user is redirected to the continue URL")

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId).isEqualTo(session.journeyId)

    val actual: CompleteResponse = journeyBuilder.getDataCollectedByBAVFEV1(session.journeyId, journeyData.credId)

    assertThat(actual.accountType).isEqualTo("personal")
    assertThat(actual.personal.get.accountName).isEqualTo(accountName.asString())
    assertThat(actual.personal.get.sortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.storedSortCode())
    assertThat(actual.personal.get.accountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(actual.personal.get.rollNumber).isEqualTo(None)
    assertThat(actual.personal.get.address.get).isEqualTo(DEFAULT_ADDRESS)
    assertThat(actual.personal.get.accountNumberWithSortCodeIsValid).isEqualTo("yes")
    assertThat(actual.personal.get.accountExists.get).isEqualTo("yes")
    assertThat(actual.personal.get.nameMatches.get).isEqualTo("yes")
    assertThat(actual.personal.get.addressMatches.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.nonConsented.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.subjectHasDeceased.get).isEqualTo("indeterminate")
    assertThat(actual.personal.get.sortCodeBankName.get).isEqualTo(DEFAULT_ACCOUNT_DETAILS.bankName.get)
    assertThat(actual.personal.get.sortCodeSupportsDirectDebit.get).isEqualTo("no")
    assertThat(actual.personal.get.sortCodeSupportsDirectCredit.get).isEqualTo("no")

    mockServer.verify(HttpRequest.request().withPath(SUREPAY_PATH), VerificationTimes.atLeast(1))
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

}
