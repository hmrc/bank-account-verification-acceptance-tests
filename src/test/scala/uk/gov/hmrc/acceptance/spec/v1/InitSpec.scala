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

package uk.gov.hmrc.acceptance.spec.v1

import org.assertj.core.api.Assertions.assertThat
import play.api.libs.json.Json
import uk.gov.hmrc.acceptance.models.{Account, Address, Business, JourneyBuilderResponse, Messages}
import uk.gov.hmrc.acceptance.models.init.InitRequest
import uk.gov.hmrc.acceptance.pages.bavfe.{BusinessAccountEntryPage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.spec.BaseSpec

import java.util.UUID

class InitSpec extends BaseSpec {

  val DEFAULT_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val DEFAULT_BUSINESS_ADDRESS: Option[Address] = Some(Address(List("22303 Darwin Turnpike"), postcode = Some("CZ0 8IW")))
  val BUSINESS_NAME: String = UUID.randomUUID().toString
  val DEFAULT_BUSINESS: Business = Business(BUSINESS_NAME, DEFAULT_BUSINESS_ADDRESS)

  Scenario("Cannot initialize a new journey with an unknown user agent") {

    val thrown = intercept[Exception] {
      initializeJourneyV1(userAgent = "unknown")
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }

  Scenario("Can override copy on the business account entry page without overriding copy on the personal account entry page") {

    Given("Updated copy is provided for the business page")

    val english = Json.obj(
      "service.name" -> "bavf-acceptance-test",
      "label.accountDetails.heading.business" -> "heading override",
      "label.accountName.business" -> "account name override",
      "label.sortCode.business" -> "sort code override",
      "label.accountNumber.business" -> "account number override",
      "label.rollNumber.optional.business" -> "roll number override",
      "hint.sortCode.business" -> "sort code hint override",
      "hint.accountNumber.business" -> "account number hint override",
      "hint.rollNumber.business" -> "roll number hint override"
    )

    val welsh = Json.obj(
      "service.name" -> "bavf-acceptance-test",
      "label.accountDetails.heading.business" -> "heading override",
      "label.accountName.business" -> "account name override",
      "label.sortCode.business" -> "sort code override",
      "label.accountNumber.business" -> "account number override",
      "label.rollNumber.optional.business" -> "roll number override",
      "hint.sortCode.business" -> "sort code hint override",
      "hint.accountNumber.business" -> "account number hint override",
      "hint.rollNumber.business" -> "roll number hint override"
    )

    val journeyData: JourneyBuilderResponse = initializeJourneyV1(InitRequest(address = DEFAULT_BUSINESS_ADDRESS, messages = Some(Messages(en = english, cy = Some(welsh)))).asJsonString())

    startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a user navigates to the business account entry page")

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    Then("the English language copy overrides are displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isEqualTo(english("label.accountDetails.heading.business"))
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isEqualTo(english("label.accountName.business"))
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isEqualTo(english("label.sortCode.business"))
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isEqualTo(english("label.accountNumber.business"))
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isEqualTo(english("label.rollNumber.optional.business"))
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isEqualTo(english("hint.sortCode.business"))
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isEqualTo(english("hint.accountNumber.business"))
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isEqualTo(english("hint.rollNumber.business"))

    When("the language is switched to Welsh")

    BusinessAccountEntryPage().switchToWelsh()

    Then("the Welsh copy overrides are displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isEqualTo(welsh("label.accountDetails.heading.business"))
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isEqualTo(welsh("label.accountName.business"))
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isEqualTo(welsh("label.sortCode.business"))
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isEqualTo(welsh("label.accountNumber.business"))
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isEqualTo(welsh("label.rollNumber.optional.business"))
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isEqualTo(welsh("hint.sortCode.business"))
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isEqualTo(welsh("hint.accountNumber.business"))
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isEqualTo(welsh("hint.rollNumber.business"))

    When("navigating to the personal account entry page")

    //Two clicks on back due to the switch to welsh
    BusinessAccountEntryPage().clickBackLink()
    BusinessAccountEntryPage().clickBackLink()
    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    Then("the Welsh copy overrides are not displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isNotEqualTo(welsh("label.accountDetails.heading.business"))
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isNotEqualTo(welsh("label.accountName.business"))
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isNotEqualTo(welsh("label.sortCode.business"))
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isNotEqualTo(welsh("label.accountNumber.business"))
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isNotEqualTo(welsh("label.rollNumber.optional.business"))
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isNotEqualTo(welsh("hint.sortCode.business"))
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isNotEqualTo(welsh("hint.accountNumber.business"))
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isNotEqualTo(welsh("hint.rollNumber.business"))

    When("the language is switched back to English")

    PersonalAccountEntryPage().switchToEnglish()

    Then("the English language copy overrides are not displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isNotEqualTo(english("label.accountDetails.heading.business"))
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isNotEqualTo(english("label.accountName.business"))
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isNotEqualTo(english("label.sortCode.business"))
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isNotEqualTo(english("label.accountNumber.business"))
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isNotEqualTo(english("label.rollNumber.optional.business"))
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isNotEqualTo(english("hint.sortCode.business"))
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isNotEqualTo(english("hint.accountNumber.business"))
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isNotEqualTo(english("hint.rollNumber.business"))
  }

  Scenario("Can override copy on the personal account entry page without overriding copy on the business account entry page") {

    Given("Updated copy is provided for the business page")

    val english = Json.obj(
      "service.name" -> "bavf-acceptance-test",
      "label.accountDetails.heading.personal" -> "heading override",
      "label.accountName.personal" -> "account name override",
      "label.sortCode.personal" -> "sort code override",
      "label.accountNumber.personal" -> "account number override",
      "label.rollNumber.optional.personal" -> "roll number override",
      "hint.sortCode.personal" -> "sort code hint override",
      "hint.accountNumber.personal" -> "account number hint override",
      "hint.rollNumber.personal" -> "roll number hint override"
    )

    val welsh = Json.obj(
      "service.name" -> "bavf-acceptance-test",
      "label.accountDetails.heading.personal" -> "heading override",
      "label.accountName.personal" -> "account name override",
      "label.sortCode.personal" -> "sort code override",
      "label.accountNumber.personal" -> "account number override",
      "label.rollNumber.optional.personal" -> "roll number override",
      "hint.sortCode.personal" -> "sort code hint override",
      "hint.accountNumber.personal" -> "account number hint override",
      "hint.rollNumber.personal" -> "roll number hint override"
    )

    val journeyData: JourneyBuilderResponse = initializeJourneyV1(InitRequest(address = DEFAULT_BUSINESS_ADDRESS, messages = Some(Messages(en = english, cy = Some(welsh)))).asJsonString())

    startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a user navigates to the personal account entry page")

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    Then("the copy overrides are displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isEqualTo(english("label.accountDetails.heading.personal"))
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isEqualTo(english("label.accountName.personal"))
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isEqualTo(english("label.sortCode.personal"))
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isEqualTo(english("label.accountNumber.personal"))
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isEqualTo(english("label.rollNumber.optional.personal"))
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isEqualTo(english("hint.sortCode.personal"))
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isEqualTo(english("hint.accountNumber.personal"))
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isEqualTo(english("hint.rollNumber.personal"))

    When("the language is switched to Welsh")

    PersonalAccountEntryPage().switchToWelsh()

    Then("the Welsh copy overrides are displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isEqualTo(welsh("label.accountDetails.heading.personal"))
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isEqualTo(welsh("label.accountName.personal"))
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isEqualTo(welsh("label.sortCode.personal"))
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isEqualTo(welsh("label.accountNumber.personal"))
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isEqualTo(welsh("label.rollNumber.optional.personal"))
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isEqualTo(welsh("hint.sortCode.personal"))
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isEqualTo(welsh("hint.accountNumber.personal"))
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isEqualTo(welsh("hint.rollNumber.personal"))

    When("navigating to the personal account entry page")

    //Two clicks on back due to the switch to welsh
    PersonalAccountEntryPage().clickBackLink()
    PersonalAccountEntryPage().clickBackLink()
    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    Then("the Welsh copy overrides are not displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isNotEqualTo(welsh("label.accountDetails.heading.personal"))
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isNotEqualTo(welsh("label.accountName.personal"))
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isNotEqualTo(welsh("label.sortCode.personal"))
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isNotEqualTo(welsh("label.accountNumber.personal"))
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isNotEqualTo(welsh("label.rollNumber.optional.personal"))
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isNotEqualTo(welsh("hint.sortCode.personal"))
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isNotEqualTo(welsh("hint.accountNumber.personal"))
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isNotEqualTo(welsh("hint.rollNumber.personal"))

    When("the language is switched back to English")

    BusinessAccountEntryPage().switchToEnglish()

    Then("the English language copy overrides are not displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isNotEqualTo(english("label.accountDetails.heading.personal"))
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isNotEqualTo(english("label.accountName.personal"))
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isNotEqualTo(english("label.sortCode.personal"))
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isNotEqualTo(english("label.accountNumber.personal"))
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isNotEqualTo(english("label.rollNumber.optional.personal"))
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isNotEqualTo(english("hint.sortCode.personal"))
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isNotEqualTo(english("hint.accountNumber.personal"))
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isNotEqualTo(english("hint.rollNumber.personal"))
  }
}
