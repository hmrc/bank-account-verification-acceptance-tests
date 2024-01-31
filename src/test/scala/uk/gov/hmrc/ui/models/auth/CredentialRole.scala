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

package uk.gov.hmrc.ui.models.auth

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}

import scala.util.Try

sealed trait CredentialRole

object CredentialRole {

  case object User extends CredentialRole

  case object Admin extends CredentialRole

  case object Assistant extends CredentialRole

  def isValid(role: String): Boolean = Try(CredentialRole(role)).isSuccess

  def apply(role: String): CredentialRole =
    role.toLowerCase match {
      case "user"      => User
      case "admin"     => Admin
      case "assistant" => Assistant
      case _           => throw new Exception(s"Invalid role: $role")
    }

  implicit val reads: Reads[CredentialRole]   = Reads[CredentialRole] { json =>
    val roleString = json.as[String]
    if (CredentialRole.isValid(roleString)) JsSuccess(CredentialRole(roleString))
    else JsError(s"Unsupported CredentialRole: $roleString")
  }
  implicit val writes: Writes[CredentialRole] = Writes[CredentialRole](role => JsString(role.toString))
}
