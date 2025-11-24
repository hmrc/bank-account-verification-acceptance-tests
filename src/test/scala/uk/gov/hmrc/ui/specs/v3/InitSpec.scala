/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ui.specs.v3

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.support.ui.ExpectedConditions.titleContains
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.ui.config.TestConfig
import uk.gov.hmrc.ui.models._
import uk.gov.hmrc.ui.models.init.{InitRequest, MaxCallConfig}
import uk.gov.hmrc.ui.pages.bavfe.{BusinessAccountEntryPage, PersonalAccountEntryPage, SelectAccountTypePage}
import uk.gov.hmrc.ui.pages.stubbed.JourneySignOutPage
import uk.gov.hmrc.ui.specs.BaseSpec

import java.util.UUID

class InitSpec extends BaseSpec {

  val DEFAULT_ACCOUNT_DETAILS: Account          = Account("40 47 84", "70872490", bankName = Some("Lloyds"))
  val DEFAULT_BUSINESS_ADDRESS: Option[Address] = Some(
    Address(List("22303 Darwin Turnpike"), postcode = Some("CZ0 8IW"))
  )
  val BUSINESS_NAME: String                     = UUID.randomUUID().toString
  val DEFAULT_BUSINESS: Business                = Business(BUSINESS_NAME, DEFAULT_BUSINESS_ADDRESS)

  Scenario("Cannot initialize a new journey with an unknown user agent") {

    val thrown = intercept[Exception] {
      journeyBuilder.initializeJourneyV3(userAgent = "unknown")
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }

  Scenario("Can initialise a journey with useNewGovUkServiceNavigation enabled") {
    Given("useNewGovUkServiceNavigation is set to true")

    val journeyData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV3(
      InitRequest(
        useNewGovUkServiceNavigation = Some(true)
      ).asJsonString()
    )

    When("A user starts a bank account entry journey")

    startGGJourney(journeyData)

    Then("The journey initializes successfully with the new navigation enabled")

    assertThat(SelectAccountTypePage().isOnPage).isTrue
  }

  Scenario(
    "Can override copy on the business account entry page without overriding copy on the personal account entry page"
  ) {

    Given("Updated copy is provided for the business page")

    val english = Json.obj(
      "service.name"                          -> "bavf-acceptance-test",
      "label.accountDetails.heading.business" -> "heading override",
      "label.accountName.business"            -> "account name override",
      "label.sortCode.business"               -> "sort code override",
      "label.accountNumber.business"          -> "account number override",
      "label.rollNumber.optional.business"    -> "roll number override",
      "hint.sortCode.business"                -> "sort code hint override",
      "hint.accountNumber.business"           -> "account number hint override",
      "hint.rollNumber.business"              -> "roll number hint override"
    )

    val welsh = Json.obj(
      "service.name"                          -> "bavf-acceptance-test",
      "label.accountDetails.heading.business" -> "heading override",
      "label.accountName.business"            -> "account name override",
      "label.sortCode.business"               -> "sort code override",
      "label.accountNumber.business"          -> "account number override",
      "label.rollNumber.optional.business"    -> "roll number override",
      "hint.sortCode.business"                -> "sort code hint override",
      "hint.accountNumber.business"           -> "account number hint override",
      "hint.rollNumber.business"              -> "roll number hint override"
    )

    val enBusinessHeading = english("label.accountDetails.heading.business").asInstanceOf[JsString].value
    val cyBusinessHeading = welsh("label.accountDetails.heading.business").asInstanceOf[JsString].value

    val journeyData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV3(
      InitRequest(address = DEFAULT_BUSINESS_ADDRESS, messages = Some(Messages(en = english, cy = Some(welsh))))
        .asJsonString()
    )

    startGGJourney(journeyData)

    When("a user navigates to the business account entry page")

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    Then("the English language copy overrides are displayed")

    BusinessAccountEntryPage().fluentWait().until(titleContains(enBusinessHeading))
    assertThat(BusinessAccountEntryPage().getHeading).isEqualTo(enBusinessHeading)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel)
      .isEqualTo(english("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel)
      .isEqualTo(english("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint)
      .isEqualTo(english("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel)
      .isEqualTo(english("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint)
      .isEqualTo(english("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel)
      .isEqualTo(english("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint)
      .isEqualTo(english("hint.rollNumber.business").asInstanceOf[JsString].value)

    When("the language is switched to Welsh")

    BusinessAccountEntryPage().switchToWelsh()

    Then("the Welsh copy overrides are displayed")

    BusinessAccountEntryPage().fluentWait().until(titleContains(cyBusinessHeading))
    assertThat(BusinessAccountEntryPage().getHeading).isEqualTo(cyBusinessHeading)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel)
      .isEqualTo(welsh("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel)
      .isEqualTo(welsh("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint)
      .isEqualTo(welsh("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel)
      .isEqualTo(welsh("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint)
      .isEqualTo(welsh("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel)
      .isEqualTo(welsh("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint)
      .isEqualTo(welsh("hint.rollNumber.business").asInstanceOf[JsString].value)

    When("navigating to the personal account entry page")

    //Two clicks on back due to the switch to welsh
    BusinessAccountEntryPage().clickBackLink()
    BusinessAccountEntryPage().clickBackLink()

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    Then("the Welsh copy overrides are not displayed")

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue
    assertThat(PersonalAccountEntryPage().getHeading).isNotEqualTo(cyBusinessHeading)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel)
      .isNotEqualTo(welsh("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel)
      .isNotEqualTo(welsh("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint)
      .isNotEqualTo(welsh("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel)
      .isNotEqualTo(welsh("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint)
      .isNotEqualTo(welsh("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel)
      .isNotEqualTo(welsh("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint)
      .isNotEqualTo(welsh("hint.rollNumber.business").asInstanceOf[JsString].value)

    When("the language is switched back to English")

    PersonalAccountEntryPage().switchToEnglish()

    Then("the English language copy overrides are not displayed")

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue
    assertThat(PersonalAccountEntryPage().getHeading).isNotEqualTo(enBusinessHeading)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel)
      .isNotEqualTo(english("label.accountName.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel)
      .isNotEqualTo(english("label.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint)
      .isNotEqualTo(english("hint.sortCode.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel)
      .isNotEqualTo(english("label.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint)
      .isNotEqualTo(english("hint.accountNumber.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel)
      .isNotEqualTo(english("label.rollNumber.optional.business").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint)
      .isNotEqualTo(english("hint.rollNumber.business").asInstanceOf[JsString].value)
  }

  Scenario(
    "Can override copy on the personal account entry page without overriding copy on the business account entry page"
  ) {

    Given("Updated copy is provided for the business page")

    val english = Json.obj(
      "service.name"                          -> "bavf-acceptance-test",
      "label.accountDetails.heading.personal" -> "heading override",
      "label.accountName.personal"            -> "account name override",
      "label.sortCode.personal"               -> "sort code override",
      "label.accountNumber.personal"          -> "account number override",
      "label.rollNumber.optional.personal"    -> "roll number override",
      "hint.sortCode.personal"                -> "sort code hint override",
      "hint.accountNumber.personal"           -> "account number hint override",
      "hint.rollNumber.personal"              -> "roll number hint override"
    )

    val welsh = Json.obj(
      "service.name"                          -> "bavf-acceptance-test",
      "label.accountDetails.heading.personal" -> "heading override",
      "label.accountName.personal"            -> "account name override",
      "label.sortCode.personal"               -> "sort code override",
      "label.accountNumber.personal"          -> "account number override",
      "label.rollNumber.optional.personal"    -> "roll number override",
      "hint.sortCode.personal"                -> "sort code hint override",
      "hint.accountNumber.personal"           -> "account number hint override",
      "hint.rollNumber.personal"              -> "roll number hint override"
    )

    val enPersonalHeading = english("label.accountDetails.heading.personal").asInstanceOf[JsString].value
    val cyPersonalHeading = welsh("label.accountDetails.heading.personal").asInstanceOf[JsString].value

    val journeyData: JourneyBuilderResponse = journeyBuilder.initializeJourneyV3(
      InitRequest(address = DEFAULT_BUSINESS_ADDRESS, messages = Some(Messages(en = english, cy = Some(welsh))))
        .asJsonString()
    )

    startGGJourney(journeyData)

    When("a user navigates to the personal account entry page")

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    Then("the copy overrides are displayed")

    PersonalAccountEntryPage().fluentWait().until(titleContains(enPersonalHeading))
    assertThat(PersonalAccountEntryPage().getHeading).isEqualTo(enPersonalHeading)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel)
      .isEqualTo(english("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel)
      .isEqualTo(english("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint)
      .isEqualTo(english("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel)
      .isEqualTo(english("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint)
      .isEqualTo(english("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel)
      .isEqualTo(english("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint)
      .isEqualTo(english("hint.rollNumber.personal").asInstanceOf[JsString].value)

    When("the language is switched to Welsh")

    PersonalAccountEntryPage().switchToWelsh()

    Then("the Welsh copy overrides are displayed")

    PersonalAccountEntryPage().fluentWait().until(titleContains(cyPersonalHeading))
    assertThat(PersonalAccountEntryPage().getHeading).isEqualTo(cyPersonalHeading)
    assertThat(PersonalAccountEntryPage().getAccountNameLabel)
      .isEqualTo(welsh("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeLabel)
      .isEqualTo(welsh("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getSortCodeHint)
      .isEqualTo(welsh("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberLabel)
      .isEqualTo(welsh("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getAccountNumberHint)
      .isEqualTo(welsh("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberLabel)
      .isEqualTo(welsh("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(PersonalAccountEntryPage().getRollNumberHint)
      .isEqualTo(welsh("hint.rollNumber.personal").asInstanceOf[JsString].value)

    When("navigating to the personal account entry page")

    //Two clicks on back due to the switch to welsh
    PersonalAccountEntryPage().clickBackLink()
    PersonalAccountEntryPage().clickBackLink()

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    Then("the Welsh copy overrides are not displayed")

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue
    assertThat(BusinessAccountEntryPage().getHeading).isNotEqualTo(cyPersonalHeading)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel)
      .isNotEqualTo(welsh("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel)
      .isNotEqualTo(welsh("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint)
      .isNotEqualTo(welsh("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel)
      .isNotEqualTo(welsh("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint)
      .isNotEqualTo(welsh("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel)
      .isNotEqualTo(welsh("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint)
      .isNotEqualTo(welsh("hint.rollNumber.personal").asInstanceOf[JsString].value)

    When("the language is switched back to English")

    BusinessAccountEntryPage().switchToEnglish()

    Then("the English language copy overrides are not displayed")

    assertThat(BusinessAccountEntryPage().isOnPage).isTrue
    assertThat(BusinessAccountEntryPage().getHeading).isNotEqualTo(enPersonalHeading)
    assertThat(BusinessAccountEntryPage().getCompanyNameLabel)
      .isNotEqualTo(english("label.accountName.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeLabel)
      .isNotEqualTo(english("label.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getSortCodeHint)
      .isNotEqualTo(english("hint.sortCode.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberLabel)
      .isNotEqualTo(english("label.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getAccountNumberHint)
      .isNotEqualTo(english("hint.accountNumber.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberLabel)
      .isNotEqualTo(english("label.rollNumber.optional.personal").asInstanceOf[JsString].value)
    assertThat(BusinessAccountEntryPage().getRollNumberHint)
      .isNotEqualTo(english("hint.rollNumber.personal").asInstanceOf[JsString].value)
  }

  Scenario("Cannot initialize a new journey with an absolute sign out link") {

    val thrown = intercept[Exception] {
      journeyBuilder.initializeJourneyV3(InitRequest(signOutUrl = Some("https://www.google.co.uk/")).asJsonString())
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }

  Scenario("The sign out link is displayed when a sign out URL is supplied") {
    Given("A sign out URL has not been supplied in the init call")

    val signOutURL                          = s"${TestConfig.environmentHost}:${TestConfig.mockServerPort()}/sign-out"
    val journeyData: JourneyBuilderResponse =
      journeyBuilder.initializeJourneyV3(InitRequest(signOutUrl = Some(signOutURL)).asJsonString())

    When("A user starts a bank account entry journey")

    startGGJourney(journeyData)

    Then("The sign out link is displayed and links to the correct sign out URL")

    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().isSignOutLinkDisplayed(isDisplayed = true)
    assertThat(SelectAccountTypePage().getSignOutLinkLocation).isEqualTo(signOutURL)

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue
    SelectAccountTypePage().isSignOutLinkDisplayed(isDisplayed = true)
    assertThat(SelectAccountTypePage().getSignOutLinkLocation).isEqualTo(signOutURL)

    PersonalAccountEntryPage().clickBackLink()
    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().selectBusinessAccount().clickContinue()

    SelectAccountTypePage().isSignOutLinkDisplayed(isDisplayed = true)
    assertThat(SelectAccountTypePage().getSignOutLinkLocation).isEqualTo(signOutURL)

    When("The user clicks on the sign out link")

    BusinessAccountEntryPage().clickSignOut()

    Then("The user is redirected to the sign out page")

    assertThat(JourneySignOutPage().isOnPage).isTrue
  }

  Scenario("The sign out link is not displayed by default") {
    Given("A sign out URL has not been supplied in the init call")

    startGGJourney(journeyBuilder.initializeJourneyV3())
    When("A user starts a bank account entry journey")

    assertThat(SelectAccountTypePage().isOnPage).isTrue

    Then("The sign out link is not displayed")
    SelectAccountTypePage().isSignOutLinkDisplayed(isDisplayed = false)

    SelectAccountTypePage().selectPersonalAccount().clickContinue()

    assertThat(PersonalAccountEntryPage().isOnPage).isTrue
    SelectAccountTypePage().isSignOutLinkDisplayed(isDisplayed = false)

    PersonalAccountEntryPage().clickBackLink()
    assertThat(SelectAccountTypePage().isOnPage).isTrue
    SelectAccountTypePage().selectBusinessAccount().clickContinue()
    SelectAccountTypePage().isSignOutLinkDisplayed(isDisplayed = false)
  }

  Scenario("Cannot initialize a new journey with a maxCallCount but no maxCallCountRedirectUrl") {

    val thrown = intercept[Exception] {
      journeyBuilder.initializeJourneyV3(
        "{\"serviceIdentifier\":\"bavf-acceptance-test\",\"continueUrl\":\"http://localhost:6001/complete\",\"messages\":{\"en\":{\"service.name\":\"bavf-acceptance-test\"}},\"bacsRequirements\":{\"directDebitRequired\":false,\"directCreditRequired\":false},\"maxCallConfig\":{\"count\":3}}"
      )
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }

  Scenario("Cannot initialize a new journey with a maxCallCountRedirectUrl but no maxCallCount") {

    val thrown = intercept[Exception] {
      journeyBuilder.initializeJourneyV3(
        "{\"serviceIdentifier\":\"bavf-acceptance-test\",\"continueUrl\":\"http://localhost:6001/complete\",\"messages\":{\"en\":{\"service.name\":\"bavf-acceptance-test\"}},\"bacsRequirements\":{\"directDebitRequired\":false,\"directCreditRequired\":false},\"maxCallConfig\":{\"redirectUrl\":\"http://localhost:6001/too/many/attempts\"}}"
      )
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }

  Scenario("Cannot initialize a new journey with a valid maxCallCount and an empty maxCallCountRedirectUrl") {

    val thrown = intercept[Exception] {
      journeyBuilder.initializeJourneyV3(InitRequest(maxCallConfig = Some(MaxCallConfig(3, ""))).asJsonString())
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }

  Scenario("Cannot initialize a new journey with a maxCallCountRedirectUrl that redirects to an external resource") {

    val thrown = intercept[Exception] {
      journeyBuilder.initializeJourneyV3(
        InitRequest(maxCallConfig = Some(MaxCallConfig(3, "https://www.google.co.uk"))).asJsonString()
      )
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }
}
