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

package uk.gov.hmrc.acceptance.utils

import okhttp3._
import play.api.libs.json._
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models._
import uk.gov.hmrc.acceptance.models.auth._
import uk.gov.hmrc.acceptance.models.init.InitRequest

import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit.SECONDS

trait JourneyBuilder {

  object BarsEndpoints {
    val REFRESH_EISCD_CACHE = "/refresh/cache/eiscd"
    val REFRESH_MODCHECK_CACHE = "/refresh/cache/modcheck"
  }

  private val okHttpClient: OkHttpClient = new OkHttpClient().newBuilder()
    .connectTimeout(10L, SECONDS)
    .readTimeout(10L, SECONDS)
    .build()

  //TODO set a "serviceIdentifier" with whitespace to check it returns with a 400 when #TAV-101 is complete?
  def initializeJourneyV1(configuration: String = InitRequest.apply().asJsonString()): JourneyBuilderResponse = {
    // **NOTE** credId can only be a maximum of 30 characters, anything longer will result in a mismatch and no journey will be found
    val credId = randomUUID().toString.slice(0, 29)
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/init")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), Json.toJson(configuration).asInstanceOf[JsString].value))
      .addHeader("Authorization", generateBearerToken(credId))
    val response = okHttpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      JourneyBuilderResponse(Json.parse(response.body.string()).as[InitResponse], credId)
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  //TODO set a "serviceIdentifier" with whitespace to check it returns with a 400 when #TAV-101 is complete?
  def initializeJourneyV2(configuration: String = InitRequest.apply().asJsonString()): JourneyBuilderResponse = {
    // **NOTE** credId can only be a maximum of 30 characters, anything longer will result in a mismatch and no journey will be found
    val credId = randomUUID().toString.slice(0, 29)
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/v2/init")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), Json.toJson(configuration).asInstanceOf[JsString].value))
      .addHeader("Authorization", generateBearerToken(credId))
    val response = okHttpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      JourneyBuilderResponse(Json.parse(response.body.string()).as[InitResponse], credId)
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  def getDataCollectedByBAVFEV1(journeyId: String, credId: String): uk.gov.hmrc.acceptance.models.response.v1.CompleteResponse = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/complete/$journeyId")
      .method("GET", null)
      .addHeader("Authorization", generateBearerToken(credId))
    val response = okHttpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      Json.parse(response.body.string()).as[uk.gov.hmrc.acceptance.models.response.v1.CompleteResponse]
    } else {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }

  def getDataCollectedByBAVFEV2(journeyId: String, credId: String): uk.gov.hmrc.acceptance.models.response.v2.CompleteResponse = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/v2/complete/$journeyId")
      .method("GET", null)
      .addHeader("Authorization", generateBearerToken(credId))
    val response = okHttpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      Json.parse(response.body.string()).as[uk.gov.hmrc.acceptance.models.response.v2.CompleteResponse]
    } else {
      throw new IllegalStateException("Unable to complete journey!")
    }
  }

  def generateBearerToken(credId: String): String = {
    val content: String = GGAuth(CredId(credId), AffinityGroup.Individual, Some(ConfidenceLevel.L50), CredentialStrength.Weak, Some(CredentialRole.User), List.empty[Enrolment]).asJsonString()
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("auth-login-api")}/government-gateway/session/login")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), content))
    val response = okHttpClient.newCall(request.build()).execute()
    response.header("Authorization")
  }

  def initializeEISCDCache(): Unit = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-reputation")}${BarsEndpoints.REFRESH_EISCD_CACHE}")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), ""))
    val response = okHttpClient.newCall(request.build()).execute()
    if (!response.isSuccessful) {
      throw new IllegalStateException("Unable to initialize EISCD Cache")
    }
  }

  def initializeModCheckCache(): Unit = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-reputation")}${BarsEndpoints.REFRESH_MODCHECK_CACHE}")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), ""))
    val response = okHttpClient.newCall(request.build()).execute()
    if (!response.isSuccessful) {
      throw new IllegalStateException("Unable to initialize Modcheck Cache")
    }
  }
}

