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

package uk.gov.hmrc.ui.pages.bavfe

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.{or, titleContains}
import uk.gov.hmrc.ui.models.init.InitRequest.DEFAULT_SERVICE_IDENTIFIER
import uk.gov.hmrc.ui.pages.BankAccountPage

case class BusinessAccountEntryPage() extends BankAccountPage {

  def enterCompanyName(companyName: String): BusinessAccountEntryPage = {
    sendKeys(By.id("companyName"), companyName)
    this
  }

  def getCompanyNameLabel: String =
    getText(By.cssSelector("label[for=companyName]"))

  def isOnPageWithServiceNavigationEnabled: Boolean =
    fluentWait().until(
      or(
        titleContains("Bank or building society account details - GOV.UK"),
        titleContains("Manylion eich cyfrif banc neu gymdeithas adeiladu - GOV.UK")
      )
    )

  override def isOnPage: Boolean =
    fluentWait().until(
      or(
        titleContains(s"Bank or building society account details - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK"),
        titleContains(s"Manylion eich cyfrif banc neu gymdeithas adeiladu - $DEFAULT_SERVICE_IDENTIFIER - GOV.UK")
      )
    )
}
