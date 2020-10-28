package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import org.mockserver.model.{HttpRequest, HttpResponse, JsonPathBody}
import org.mockserver.verify.VerificationTimes
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.pages.{ExampleFrontendDonePage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.stubs.transunion.{CallValidateResponseBuilder, IdentityCheckBuilder}
import uk.gov.hmrc.acceptance.utils._
import uk.gov.hmrc.acceptance.utils.types.{Account, Address, Individual, InitJourney}

class PersonalAddressSpec extends BaseSpec with MockServer {

  val DEFAULT_NAME: Individual = Individual(title = Some("Mr"), firstName = Some("Patrick"), lastName = Some("O'Conner-Smith"))
  val DEFAULT_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490")
  val DEFAULT_ADDRESS: Address = Address(List("2664 Little Darwin"), postcode = Some("CZ0 9AV"))

  Scenario("Personal Bank Account Verification with address is successful") {
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
        .withBody(
          new CallValidateResponseBuilder()
            .setInputIndividualData(DEFAULT_NAME)
            .setInputAddress(DEFAULT_ADDRESS)
            .identityCheck(new IdentityCheckBuilder()
              .nameMatched(DEFAULT_NAME.asString())
              .currentAddressMatched(DEFAULT_ADDRESS)
              .build()
            )
            .build()
        )
        .withStatusCode(200)
    )

    Given("I want to collect and validate personal bank account details")

    val journeyId: String = initializeJourney(InitJourney(address = Some(DEFAULT_ADDRESS)).asJsonString())

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='RequestReceived' " +
            "&& @.detail.input=='Request to /api/init'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    go to journeyStartPage(journeyId)

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='RequestReceived' " +
            s" && @.detail.input=='Request to /bank-account-verification/start/$journeyId'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='RequestReceived' " +
            s"&& @.detail.input=='Request to /bank-account-verification/verify/personal/$journeyId'" +
            ")]")
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

    assertThat(webDriver.getCurrentUrl).isEqualTo(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId")
    assertThat(ExampleFrontendDonePage().getAccountType).isEqualTo("personal")
    assertThat(ExampleFrontendDonePage().getAccountName).isEqualTo(DEFAULT_NAME.asString())
    assertThat(ExampleFrontendDonePage().getSortCode).isEqualTo(DEFAULT_ACCOUNT_DETAILS.sortCode)
    assertThat(ExampleFrontendDonePage().getAccountNumber).isEqualTo(DEFAULT_ACCOUNT_DETAILS.accountNumber)
    assertThat(ExampleFrontendDonePage().getRollNumber).isEmpty()
    assertThat(ExampleFrontendDonePage().getAddress).isEqualTo(DEFAULT_ADDRESS.asStringWithCR())
    assertThat(ExampleFrontendDonePage().getValidationResult).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountExists).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountNameMatched).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountAddressMatched).isEqualTo("yes")
    assertThat(ExampleFrontendDonePage().getAccountNonConsented).isEqualTo("no")
    assertThat(ExampleFrontendDonePage().getAccountOwnerDeceased).isEqualTo("indeterminate")
    assertThat(ExampleFrontendDonePage().getBankName).isEqualTo("Lloyds")

    mockServer.verify(HttpRequest.request().withPath(TRANSUNION_PATH), VerificationTimes.atLeast(1))

    mockServer.verify(
      HttpRequest.request()
        .withPath("/write/audit")
        .withBody(
          JsonPathBody.jsonPath("$[?(" +
            "@.auditType=='RequestReceived' " +
            s"&& @.detail.input=='Request to /api/complete/$journeyId'" +
            ")]")
        ),
      VerificationTimes.atLeast(1)
    )
  }
}
