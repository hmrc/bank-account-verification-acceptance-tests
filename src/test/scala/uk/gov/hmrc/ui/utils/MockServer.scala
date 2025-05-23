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

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.{HttpRequest, HttpResponse}
import org.scalatest.concurrent.Eventually
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import uk.gov.hmrc.ui.config.TestConfig

trait MockServer extends AnyFeatureSpec with Eventually with BeforeAndAfterAll with BeforeAndAfterEach {

  val MODULR_PATH = "/api-sandbox-token/account-name-check"

  private val mockServerPort           = TestConfig.mockServerPort()
  lazy val mockServer: ClientAndServer = ClientAndServer.startClientAndServer(mockServerPort)

  override def beforeAll(): Unit =
    super.beforeAll()

  override def beforeEach: Unit = {
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath("/write/audit")
      )
      .respond(
        HttpResponse
          .response()
          .withStatusCode(200)
      )

    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("POST")
          .withPath("/write/audit/merged")
      )
      .respond(
        HttpResponse
          .response()
          .withStatusCode(200)
      )

    //Continue URL
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("GET")
          .withPath("/complete/.*")
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "text/html")
          .withBody(s"""
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
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("GET")
          .withPath("/sign-out")
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "text/html")
          .withBody(s"""
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

    //Continue URL
    mockServer
      .when(
        HttpRequest
          .request()
          .withMethod("GET")
          .withPath("/too/many/attempts/.*")
      )
      .respond(
        HttpResponse
          .response()
          .withHeader("Content-Type", "text/html")
          .withBody(s"""
             |<!DOCTYPE html>
             |<html lang="en">
             |<head>
             |	<meta charset="utf-8">
             |	<title>Too many attempts</title>
             |</head>
             |<body>
             |	<h1>Returned to calling service</h1>
             |  <p>Too many attempts to enter bank account details for
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
  }

  override def afterEach: Unit =
    mockServer.reset()

  override def afterAll(): Unit = {
    mockServer.stop()
    super.afterAll()
  }
}
