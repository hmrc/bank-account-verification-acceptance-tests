package uk.gov.hmrc.acceptance.spec

import org.assertj.core.api.Assertions.assertThat
import uk.gov.hmrc.acceptance.pages._
import uk.gov.hmrc.acceptance.tags.Accessibility

class AccessibilitySpec extends BaseSpec {

  Scenario("Accessibility - Personal Bank Account Verification errors", Accessibility) {
    Given("I want to collect and validate a customers bank account details")

    startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().clickContinue()

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("accountType")
    assertThatRadioButtonErrorMessageIsDisplayed("account-type")

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    When("a customer does not enter all required information")

    PersonalAccountEntryPage().clickContinue()

    Then("errors are displayed to the user")

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(3)
    assertThatErrorSummaryLinkExists("accountName")
    assertThatInputFieldErrorMessageExists("accountName")
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")
    assertThatErrorSummaryLinkExists("accountNumber")
    assertThatInputFieldErrorMessageExists("accountNumber")
  }

  Scenario("Accessibility - Company Bank Account Verification errors", Accessibility) {
    Given("I want to collect and validate a companies bank account details")

    startGGJourney(initializeJourney())

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    SelectAccountTypePage().clickContinue()

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(1)
    assertThatErrorSummaryLinkExists("accountType")
    assertThatRadioButtonErrorMessageIsDisplayed("account-type")

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    When("a company representative does not enter all required information")

    PersonalAccountEntryPage().clickContinue()

    Then("errors are displayed to the user")

    assertThat(PersonalAccountEntryPage().errorMessageSummaryCount()).isEqualTo(3)
    assertThatErrorSummaryLinkExists("companyName")
    assertThatInputFieldErrorMessageExists("companyName")
    assertThatErrorSummaryLinkExists("sortCode")
    assertThatInputFieldErrorMessageExists("sortCode")
    assertThatErrorSummaryLinkExists("accountNumber")
    assertThatInputFieldErrorMessageExists("accountNumber")
  }
}
