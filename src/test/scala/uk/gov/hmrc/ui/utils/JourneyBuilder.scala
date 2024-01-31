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

package uk.gov.hmrc.ui.utils

import play.api.libs.json._
import uk.gov.hmrc.ui.config.TestConfig
import uk.gov.hmrc.ui.models._
import uk.gov.hmrc.ui.models.auth._
import uk.gov.hmrc.ui.models.init.InitRequest

import java.util.UUID.randomUUID
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class JourneyBuilder extends HttpClient {

  val applicationJson  = "application/json"
  val defaultUserAgent = "bavfe-acceptance-tests"

  object HeaderNames {
    val authorisation = "Authorization"
    val xRequestId    = "X-Request-ID"
    val userAgent     = "User-Agent"
    val contentType   = "Content-Type"
  }

  object BarsEndpoints {
    val REFRESH_EISCD_CACHE    = "/refresh/cache/eiscd"
    val REFRESH_MODCHECK_CACHE = "/refresh/cache/modcheck"
  }

  //TODO set a "serviceIdentifier" with whitespace to check it returns with a 400 when #TAV-101 is complete?
  def initializeJourneyV1(
    configuration: String = InitRequest.apply().asJsonString(),
    userAgent: String = defaultUserAgent
  ): JourneyBuilderResponse = {
    // **NOTE** credId can only be a maximum of 30 characters, anything longer will result in a mismatch and no journey will be found
    val credId = randomUUID().toString.slice(0, 29)

    val response = Await.result(
      post(
        s"${TestConfig.apiUrl("bank-account-verification")}/init",
        configuration,
        HeaderNames.contentType   -> applicationJson,
        HeaderNames.userAgent     -> userAgent,
        HeaderNames.authorisation -> generateBearerToken(credId)
      ),
      10.seconds
    )

    if (response.status.toString.startsWith("2")) {
      JourneyBuilderResponse(Json.parse(response.body).as[InitResponse], credId)
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  def generateBearerToken(credId: String): String = {
    val content: String = GGAuth(
      CredId(credId),
      AffinityGroup.Individual,
      Some(ConfidenceLevel.L50),
      CredentialStrength.Strong,
      Some(CredentialRole.User),
      List.empty[Enrolment]
    ).asJsonString()

    val response = Await.result(
      post(
        s"${TestConfig.apiUrl("auth-login-api")}/government-gateway/session/login",
        content,
        HeaderNames.contentType -> applicationJson
      ),
      10.seconds
    )

    response.header("Authorization").get
  }

  //TODO set a "serviceIdentifier" with whitespace to check it returns with a 400 when #TAV-101 is complete?
  def initializeJourneyV2(
    configuration: String = InitRequest.apply().asJsonString(),
    userAgent: String = defaultUserAgent
  ): JourneyBuilderResponse = {
    // **NOTE** credId can only be a maximum of 30 characters, anything longer will result in a mismatch and no journey will be found
    val credId   = randomUUID().toString.slice(0, 29)
    val response = Await.result(
      post(
        s"${TestConfig.apiUrl("bank-account-verification")}/v2/init",
        configuration,
        HeaderNames.contentType   -> applicationJson,
        HeaderNames.userAgent     -> userAgent,
        HeaderNames.authorisation -> generateBearerToken(credId)
      ),
      10.seconds
    )

    if (response.status.toString.startsWith("2")) {
      JourneyBuilderResponse(Json.parse(response.body).as[InitResponse], credId)
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  def initializeJourneyV3(
    configuration: String = InitRequest.apply().asJsonString(),
    userAgent: String = defaultUserAgent
  ): JourneyBuilderResponse = {
    // **NOTE** credId can only be a maximum of 30 characters, anything longer will result in a mismatch and no journey will be found
    val credId   = randomUUID().toString.slice(0, 29)
    val response = Await.result(
      post(
        s"${TestConfig.apiUrl("bank-account-verification")}/v3/init",
        configuration,
        HeaderNames.contentType   -> applicationJson,
        HeaderNames.userAgent     -> userAgent,
        HeaderNames.authorisation -> generateBearerToken(credId)
      ),
      10.seconds
    )

    if (response.status.toString.startsWith("2")) {
      JourneyBuilderResponse(Json.parse(response.body).as[InitResponse], credId)
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  def getDataCollectedByBAVFEV1(
    journeyId: String,
    credId: String,
    userAgent: String = defaultUserAgent
  ): uk.gov.hmrc.ui.models.response.v1.CompleteResponse = {

    val response = Await.result(
      get(
        s"${TestConfig.apiUrl("bank-account-verification")}/complete/$journeyId",
        HeaderNames.userAgent     -> userAgent,
        HeaderNames.authorisation -> generateBearerToken(credId)
      ),
      10.seconds
    )

    if (response.status.toString.startsWith("2")) {
      Json.parse(response.body).as[uk.gov.hmrc.ui.models.response.v1.CompleteResponse]
    } else {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }

  def getDataCollectedByBAVFEV2(
    journeyId: String,
    credId: String,
    userAgent: String = defaultUserAgent
  ): uk.gov.hmrc.ui.models.response.v2.CompleteResponse = {

    val response = Await.result(
      get(
        s"${TestConfig.apiUrl("bank-account-verification")}/v2/complete/$journeyId",
        HeaderNames.userAgent     -> userAgent,
        HeaderNames.authorisation -> generateBearerToken(credId)
      ),
      10.seconds
    )

    if (response.status.toString.startsWith("2")) {
      Json.parse(response.body).as[uk.gov.hmrc.ui.models.response.v2.CompleteResponse]
    } else {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }

  def getDataCollectedByBAVFEV3(
    journeyId: String,
    credId: String,
    userAgent: String = defaultUserAgent
  ): uk.gov.hmrc.ui.models.response.v3.CompleteResponse = {

    val response = Await.result(
      get(
        s"${TestConfig.apiUrl("bank-account-verification")}/v3/complete/$journeyId",
        HeaderNames.userAgent     -> userAgent,
        HeaderNames.authorisation -> generateBearerToken(credId)
      ),
      10.seconds
    )

    if (response.status.toString.startsWith("2")) {
      Json.parse(response.body).as[uk.gov.hmrc.ui.models.response.v3.CompleteResponse]
    } else {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }

  def initializeEISCDCache() = {
    val response = Await.result(
      post(
        s"${TestConfig.apiUrl("bank-account-reputation")}${BarsEndpoints.REFRESH_EISCD_CACHE}",
        "",
        HeaderNames.contentType -> applicationJson
      ),
      10.seconds
    )

    if (!response.status.toString.startsWith("2")) {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }

  def initializeModCheckCache() = {
    val response = Await.result(
      post(
        s"${TestConfig.apiUrl("bank-account-reputation")}${BarsEndpoints.REFRESH_MODCHECK_CACHE}",
        "",
        HeaderNames.contentType -> applicationJson
      ),
      10.seconds
    )

    if (!response.status.toString.startsWith("2")) {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }
}
