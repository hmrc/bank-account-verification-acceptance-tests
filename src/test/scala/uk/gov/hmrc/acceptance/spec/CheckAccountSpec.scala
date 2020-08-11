package uk.gov.hmrc.acceptance.spec

import uk.gov.hmrc.acceptance.pages.AccountEntryPage

class CheckAccountSpec extends BaseSpec {

  Scenario("Bank Account Verification happy path") {
    Given("I want to collect and validate a customers bank account details")
    go to initializeJourneyPage()
    assert(AccountEntryPage().isOnPage)

    When("a customer enters all required information and clicks continue")
    AccountEntryPage()
      .enterAccountName("Patrick O'Conner")
      .enterSortCode("07-00-93")
      .enterAccountNumber("33333334")
      .enterRollNumber("NW/1356")
    //TODO click on continue and check return URL
//      .clickContinue()

    Then("customer is redirected to continue URL")
  }

}
