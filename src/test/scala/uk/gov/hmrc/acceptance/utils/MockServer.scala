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

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.{HttpRequest, HttpResponse}
import org.scalatest.concurrent.Eventually
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import uk.gov.hmrc.acceptance.config.TestConfig

import java.util.UUID

trait MockServer extends AnyFeatureSpec
  with Eventually
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  val SUREPAY_PATH = "/surepay/v1/gateway"
  val TRANSUNION_PATH = "/callvalidateapi"
  val CREDITSAFE_PATH = "/Match"

  private val mockServerPort = TestConfig.mockServerPort()
  lazy val mockServer: ClientAndServer = ClientAndServer.startClientAndServer(mockServerPort)

  override def beforeAll() {
    super.beforeAll()
  }

  override def beforeEach: Unit = {
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath("/write/audit")
    ).respond(
      HttpResponse.response()
        .withStatusCode(200)
    )
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath("/write/audit/merged")
    ).respond(
      HttpResponse.response()
        .withStatusCode(200)
    )
    mockServer.when(
      HttpRequest.request()
        .withMethod("POST")
        .withPath("/surepay/oauth/client_credential/accesstoken")
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "application/json")
        .withBody(s"""{"access_token" : "${UUID.randomUUID().toString}", "expires_in" : "3599", "token_type" : "BearerToken" }""".stripMargin)
        .withStatusCode(200)
    )
    //Continue URL
    mockServer.when(
      HttpRequest.request()
        .withMethod("GET")
        .withPath("/complete/.*")
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "text/html")
        .withBody(
          s"""
             |<!DOCTYPE html>
             |<html lang="en">
             |<head>
             |	<meta charset="utf-8">
             |	<title>Journey complete</title>
             |</head>
             |<body>
             |	<h1>Journey Complete</h1>
             |  <p>Journey has been completed for
             |    <span id="journeyId">
             |      <script type="text/javascript">
             |      let journeyId = this.window.location.pathname.split('/').slice(-1)[0];
             |      document.write(journeyId);
             |      </script>
             |    </span>
             |  </p>
             |</body>
             |</html>
             |""".stripMargin)
        .withStatusCode(200)
    )
    //Sign out page
    mockServer.when(
      HttpRequest.request()
        .withMethod("GET")
        .withPath("/sign-out")
    ).respond(
      HttpResponse.response()
        .withHeader("Content-Type", "text/html")
        .withBody(
          s"""
             |<!DOCTYPE html>
             |<html lang="en">
             |<head>
             |	<meta charset="utf-8">
             |	<title>Signed out</title>
             |</head>
             |<body>
             |	<h1>Sign Out</h1>
             |  <p>Successfully signed out</p>
             |</body>
             |</html>
             |""".stripMargin)
        .withStatusCode(200)
    )
  }

  override def afterEach: Unit = {
    mockServer.reset()
  }

  override def afterAll() {
    mockServer.stop()
    super.afterAll()
  }
}
