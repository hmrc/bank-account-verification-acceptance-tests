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

package uk.gov.hmrc.acceptance.models.init

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.init.InitRequest.{DEFAULT_MESSAGES, DEFAULT_SERVICE_IDENTIFIER}
import uk.gov.hmrc.acceptance.models.{Address, Messages}

object InitRequest {
  val DEFAULT_SERVICE_IDENTIFIER = "bavf-acceptance-test"
  val DEFAULT_MESSAGES: Messages = Messages(en = Json.obj("service.name" -> DEFAULT_SERVICE_IDENTIFIER), None)

  implicit val initJsonFormat: OFormat[InitRequest] = Json.format[InitRequest]
}

case class InitRequest(serviceIdentifier: String = DEFAULT_SERVICE_IDENTIFIER,
                       continueUrl: String = s"${TestConfig.url("bank-account-verification-frontend-example")}/done",
                       prepopulatedData: Option[PrepopulatedData] = None,
                       address: Option[Address] = None,
                       messages: Option[Messages] = Some(DEFAULT_MESSAGES),
                       customisationsUrl: Option[String] = None,
                       bacsRequirements: Option[InitBACSRequirements] = Some(InitBACSRequirements(directDebitRequired = false, directCreditRequired = false)),
                       timeoutConfig: Option[InitRequestTimeoutConfig] = None) {

  def asJsonString(): String = {
    Json.toJson(this).toString()
  }

}
