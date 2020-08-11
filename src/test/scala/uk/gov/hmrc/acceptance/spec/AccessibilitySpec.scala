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
    AccountEntryPage().assertErrorMessage("accountName", Some("Account name is required"))
    AccountEntryPage().assertErrorSummaryLink("accountName", Some("Account name is required"))
    AccountEntryPage().assertErrorMessage("sortCode", Some("Sort code is required"))
    AccountEntryPage().assertErrorSummaryLink("sortCode", Some("Sort code is required"))
    AccountEntryPage().assertErrorMessage("accountNumber", Some("Account number is required"))
    AccountEntryPage().assertErrorSummaryLink("accountNumber", Some("Account number is required"))
    AccountEntryPage().assertErrorMessageSummaryCountIsEqualTo(6)
  }
}
