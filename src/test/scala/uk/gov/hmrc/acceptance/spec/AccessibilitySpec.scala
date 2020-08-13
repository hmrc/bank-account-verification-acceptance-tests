package uk.gov.hmrc.acceptance.spec

import uk.gov.hmrc.acceptance.pages._
import uk.gov.hmrc.acceptance.tags.Accessibility

class AccessibilitySpec extends BaseSpec {

  Scenario("Accessibility - Bank Account Verification errors", Accessibility) {
    Given("I want to collect and validate a customers bank account details")
    go to initializeJourneyPage()
    assert(AccountEntryPage().isOnPage)

    When("a customer does not enter all required information")
    AccountEntryPage().clickContinue()

    Then("errors are displayed to the user")
    AccountEntryPage().assertErrorMessage("accountName", Some("Enter the name on the account"))
    AccountEntryPage().assertErrorSummaryLink("accountName", Some("Enter the name on the account"))
    AccountEntryPage().assertErrorMessage("sortCode", Some("Enter a sort code"))
    AccountEntryPage().assertErrorSummaryLink("sortCode", Some("Enter a sort code"))
    AccountEntryPage().assertErrorMessage("accountNumber", Some("Enter an account number"))
    AccountEntryPage().assertErrorSummaryLink("accountNumber", Some("Enter an account number"))
    AccountEntryPage().assertErrorMessageSummaryCountIsEqualTo(6)
  }
}
