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

package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

sealed abstract class CredentialStrength(val name: String)

object CredentialStrength {

  case object Strong extends CredentialStrength("strong")

  case object Weak extends CredentialStrength("weak")

  case object None extends CredentialStrength("none")

  def fromString(credentialStrength: String): CredentialStrength = credentialStrength match {
    case Strong.name => Strong
    case Weak.name => Weak
    case None.name => None
    case _ => throw new NoSuchElementException(s"Illegal credential strength: $credentialStrength")
  }

  implicit val format: Format[CredentialStrength] = {
    val reads = Reads[CredentialStrength] { json =>
      Try {
        fromString(json.as[String])
      } match {
        case Success(credStrength) => JsSuccess(credStrength)
        case Failure(ex) => JsError(ex.getMessage)
      }
    }
    val writes = Writes[CredentialStrength] { credStrength => JsString(credStrength.name) }
    Format(reads, writes)
  }

}
