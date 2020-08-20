package uk.gov.hmrc.acceptance.spec

import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.pages.AccountEntryPage

class CheckAccountSpec extends BaseSpec {

  Scenario("Bank Account Verification happy path") {
    Given("I want to collect and validate a customers bank account details")
    val journeyId: String = initializeJourney()
    go to journeyStartPage(journeyId)
    assert(AccountEntryPage().isOnPage)

    When("a customer enters all required information and clicks continue")
    AccountEntryPage()
      .enterAccountName("Patrick O'Conner")
      .enterSortCode("07-00-93")
      .enterAccountNumber("33333334")
      .enterRollNumber("NW/1356")
      .clickContinue()

    Then("customer is redirected to continue URL")
    assert(webDriver.getCurrentUrl.equals(s"${TestConfig.url("bank-account-verification-frontend-example")}/done/$journeyId"))
  }

}
