package uk.gov.hmrc.acceptance.spec

import uk.gov.hmrc.acceptance.pages._
import uk.gov.hmrc.acceptance.tags.Accessibility

class AccessibilitySpec extends BaseSpec {

  Scenario("Accessibility - Bank Account Verification errors", Accessibility) {
    Given("I want to collect and validate a customers bank account details")
    go to journeyStartPage(initializeJourney())
    assert(SelectAccountTypePage().isOnPage)
    SelectAccountTypePage().clickContinue()
    AccountEntryPage().assertErrorMessageSummaryCountIsEqualTo(1)
    SelectAccountTypePage().assertErrorSummaryLinkExists("accountType")
    SelectAccountTypePage().assertRadioButtonErrorMessageExists("account-type")
    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer does not enter all required information")
    AccountEntryPage().clickContinue()

    Then("errors are displayed to the user")
    AccountEntryPage().assertErrorMessageSummaryCountIsEqualTo(3)
    AccountEntryPage().assertErrorSummaryLinkExists("accountName")
    AccountEntryPage().assertInputFieldErrorMessageExists("accountName")
    AccountEntryPage().assertErrorSummaryLinkExists("sortCode")
    AccountEntryPage().assertInputFieldErrorMessageExists("sortCode")
    AccountEntryPage().assertErrorSummaryLinkExists("accountNumber")
    AccountEntryPage().assertInputFieldErrorMessageExists("accountNumber")
  }
}
