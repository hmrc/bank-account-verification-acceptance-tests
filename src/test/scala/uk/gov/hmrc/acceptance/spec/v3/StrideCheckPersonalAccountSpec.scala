/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.acceptance.spec.v3

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.acceptance.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.acceptance.models.response.v3.CompleteResponse
import uk.gov.hmrc.acceptance.models.{Account, Individual}
import uk.gov.hmrc.acceptance.pages.bavfe.{BusinessAccountEntryPage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.pages.stubbed.JourneyCompletePage
import uk.gov.hmrc.acceptance.spec.BaseSpec
import uk.gov.hmrc.acceptance.utils.MockServer

class StrideCheckPersonalAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_NAME: Individual = Individual(title = Some("Mr"), firstName = Some("Paddy"), lastName = Some("O'Conner-Smith"))
  val DEFAULT_BANK_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490", bankName = Some("Lloyds"))

  Scenario("Personal Bank Account Verification successful bank check with Stride") {
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

    val journeyData = initializeJourneyV3()
    val session = startStrideJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue

    When("a company representative enters all required information and clicks continue")

    PersonalAccountEntryPage()
      .enterAccountName(DEFAULT_NAME.asString())
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

    assertThat(JourneyCompletePage().isOnPage).isTrue
    assertThat(JourneyCompletePage().getJourneyId()).isEqualTo(session.journeyId)

    val actual: CompleteResponse = getDataCollectedByBAVFEV3(session.journeyId, journeyData.credId)

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
}
