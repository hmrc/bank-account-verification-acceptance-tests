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

package uk.gov.hmrc.acceptance.spec.v2

import org.assertj.core.api.Assertions.assertThat
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.acceptance.models._
import uk.gov.hmrc.acceptance.models.init.InitRequest
import uk.gov.hmrc.acceptance.pages.bavfe.{BusinessAccountEntryPage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.acceptance.spec.BaseSpec
import uk.gov.hmrc.acceptance.utils.MockServer

import java.util.UUID

class InitSpec extends BaseSpec with MockServer {

  val DEFAULT_ACCOUNT_DETAILS: Account = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val DEFAULT_BUSINESS_ADDRESS: Option[Address] = Some(Address(List("22303 Darwin Turnpike"), postcode = Some("CZ0 8IW")))
  val BUSINESS_NAME: String = UUID.randomUUID().toString
  val DEFAULT_BUSINESS: Business = Business(BUSINESS_NAME, DEFAULT_BUSINESS_ADDRESS)

  Scenario("Cannot initialize a new journey with an unknown user agent") {

    val thrown = intercept[Exception] {
      initializeJourneyV2(userAgent = "unknown")
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

    val journeyData: JourneyBuilderResponse = initializeJourneyV2(InitRequest(address = DEFAULT_BUSINESS_ADDRESS, messages = Some(Messages(en = english, cy = Some(welsh)))).asJsonString())

    startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a user navigates to the business account entry page")

    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    Then("the English language copy overrides are displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isEqualTo(english("label.accountDetails.heading.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isEqualTo(english("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isEqualTo(english("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isEqualTo(english("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isEqualTo(english("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isEqualTo(english("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isEqualTo(english("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isEqualTo(english("hint.rollNumber.business").asInstanceOf[JsString].value)

    When("the language is switched to Welsh")

    BusinessAccountEntryPage().switchToWelsh()

    Then("the Welsh copy overrides are displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isEqualTo(welsh("label.accountDetails.heading.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isEqualTo(welsh("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isEqualTo(welsh("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isEqualTo(welsh("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isEqualTo(welsh("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isEqualTo(welsh("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isEqualTo(welsh("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isEqualTo(welsh("hint.rollNumber.business").asInstanceOf[JsString].value)

    When("navigating to the personal account entry page")

    //Two clicks on back due to the switch to welsh
    BusinessAccountEntryPage().clickBackLink()
    BusinessAccountEntryPage().clickBackLink()
    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    Then("the Welsh copy overrides are not displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isNotEqualTo(welsh("label.accountDetails.heading.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isNotEqualTo(welsh("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isNotEqualTo(welsh("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isNotEqualTo(welsh("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isNotEqualTo(welsh("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isNotEqualTo(welsh("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isNotEqualTo(welsh("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isNotEqualTo(welsh("hint.rollNumber.business").asInstanceOf[JsString].value)

    When("the language is switched back to English")

    PersonalAccountEntryPage().switchToEnglish()

    Then("the English language copy overrides are not displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isNotEqualTo(english("label.accountDetails.heading.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isNotEqualTo(english("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isNotEqualTo(english("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isNotEqualTo(english("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isNotEqualTo(english("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isNotEqualTo(english("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isNotEqualTo(english("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isNotEqualTo(english("hint.rollNumber.business").asInstanceOf[JsString].value)
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

    val journeyData: JourneyBuilderResponse = initializeJourneyV2(InitRequest(address = DEFAULT_BUSINESS_ADDRESS, messages = Some(Messages(en = english, cy = Some(welsh)))).asJsonString())

    startGGJourney(journeyData)

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    When("a user navigates to the personal account entry page")

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    Then("the copy overrides are displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isEqualTo(english("label.accountDetails.heading.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isEqualTo(english("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isEqualTo(english("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isEqualTo(english("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isEqualTo(english("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isEqualTo(english("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isEqualTo(english("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isEqualTo(english("hint.rollNumber.personal").asInstanceOf[JsString].value)

    When("the language is switched to Welsh")

    PersonalAccountEntryPage().switchToWelsh()

    Then("the Welsh copy overrides are displayed")

    assertThat(PersonalAccountEntryPage().getHeading).isEqualTo(welsh("label.accountDetails.heading.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel).isEqualTo(welsh("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel).isEqualTo(welsh("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint).isEqualTo(welsh("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel).isEqualTo(welsh("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint).isEqualTo(welsh("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel).isEqualTo(welsh("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint).isEqualTo(welsh("hint.rollNumber.personal").asInstanceOf[JsString].value)

    When("navigating to the personal account entry page")

    //Two clicks on back due to the switch to welsh
    PersonalAccountEntryPage().clickBackLink()
    PersonalAccountEntryPage().clickBackLink()
    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    Then("the Welsh copy overrides are not displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isNotEqualTo(welsh("label.accountDetails.heading.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isNotEqualTo(welsh("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isNotEqualTo(welsh("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isNotEqualTo(welsh("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isNotEqualTo(welsh("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isNotEqualTo(welsh("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isNotEqualTo(welsh("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isNotEqualTo(welsh("hint.rollNumber.personal").asInstanceOf[JsString].value)

    When("the language is switched back to English")

    BusinessAccountEntryPage().switchToEnglish()

    Then("the English language copy overrides are not displayed")

    assertThat(BusinessAccountEntryPage().getHeading).isNotEqualTo(english("label.accountDetails.heading.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel).isNotEqualTo(english("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel).isNotEqualTo(english("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint).isNotEqualTo(english("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel).isNotEqualTo(english("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint).isNotEqualTo(english("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel).isNotEqualTo(english("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint).isNotEqualTo(english("hint.rollNumber.personal").asInstanceOf[JsString].value)
  }
}
