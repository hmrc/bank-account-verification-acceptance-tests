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

package uk.gov.hmrc.ui.models.response.v3

import play.api.libs.json.{Json, OWrites, Reads}

case class CompleteResponse(accountType: String, personal: Option[PersonalComplete], business: Option[BusinessComplete])

object CompleteResponse {
  implicit val writes: OWrites[CompleteResponse] = Json.writes[CompleteResponse]
  implicit val reads: Reads[CompleteResponse]    = Json.reads[CompleteResponse]
}
