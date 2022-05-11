/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json._

import scala.util.Try

sealed trait AffinityGroup

object AffinityGroup {

  case object Individual extends AffinityGroup

  case object Organisation extends AffinityGroup

  case object Agent extends AffinityGroup

  def isValid(name: String): Boolean = Try(AffinityGroup(name)).isSuccess

  def apply(name: String): AffinityGroup =
    name match {
      case "Individual"   => Individual
      case "Organisation" => Organisation
      case "Agent"        => Agent
      case _              => throw new Exception(s"Invalid affinity group: $name")
    }

  implicit val reads: Reads[AffinityGroup]   = Reads[AffinityGroup] { json =>
    val nameString = json.as[String]
    if (AffinityGroup.isValid(nameString)) JsSuccess(AffinityGroup(nameString))
    else JsError(s"Unsupported AffinityGroup: $nameString")
  }
  implicit val writes: Writes[AffinityGroup] = Writes[AffinityGroup] { affinityGroup =>
    JsString(affinityGroup.toString)
  }
}
