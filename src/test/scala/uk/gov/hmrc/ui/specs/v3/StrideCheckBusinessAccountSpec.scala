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
import uk.gov.hmrc.ui.models.response.v3.CompleteResponse
import uk.gov.hmrc.ui.pages.bavfe.{BusinessAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.ui.pages.stubbed.JourneyCompletePage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MockServer

class StrideCheckBusinessAccountSpec extends BaseSpec with MockServer {

  val DEFAULT_COMPANY_NAME                      = "P@cking & $orting"
  val DEFAULT_BUILDING_SOCIETY_DETAILS: Account = Account("07-00-93", "33333334", Some("NW/1356"), Some("Lloyds"))
  val DEFAULT_BANK_ACCOUNT_DETAILS: Account     = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val HMRC_ACCOUNT_DETAILS: Account             = Account("08 32 10", "12001039")

  Scenario("Business Bank Account Verification successful bank check with Stride") {
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
    val session     = startStrideJourney(journeyData)

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
}
