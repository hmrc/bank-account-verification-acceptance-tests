/*
 * Copyright 2021 HM Revenue & Customs
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
    assertThatRadioButtonErrorMessageIsDisplayed("accountType")

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
    assertThatRadioButtonErrorMessageIsDisplayed("accountType")

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
