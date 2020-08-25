package uk.gov.hmrc.acceptance.spec

import uk.gov.hmrc.acceptance.pages._
import uk.gov.hmrc.acceptance.tags.Accessibility

class AccessibilitySpec extends BaseSpec {

  Scenario("Accessibility - Bank Account Verification errors", Accessibility) {
    Given("I want to collect and validate a customers bank account details")
    go to journeyStartPage(initializeJourney())
    assert(SelectAccountTypePage().isOnPage)
    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer does not enter all required information")
    AccountEntryPage().clickContinue()

    Then("errors are displayed to the user")
    AccountEntryPage().assertErrorMessageExists("accountName")
    AccountEntryPage().assertErrorSummaryLinkExists("accountName")
    AccountEntryPage().assertErrorMessageExists("sortCode")
    AccountEntryPage().assertErrorSummaryLinkExists("sortCode")
    AccountEntryPage().assertErrorMessageExists("accountNumber")
    AccountEntryPage().assertErrorSummaryLinkExists("accountNumber")
    AccountEntryPage().assertErrorMessageSummaryCountIsEqualTo(3)
  }
}
