package uk.gov.hmrc.acceptance.utils

import java.util.UUID

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.{HttpRequest, HttpResponse}
import org.scalatest.concurrent.Eventually
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import uk.gov.hmrc.acceptance.config.TestConfig


trait MockServer extends AnyFeatureSpec
  with Eventually
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

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
  }

  override def afterEach: Unit = {
    mockServer.reset()
  }

  override def afterAll() {
    mockServer.stop()
    super.afterAll()
  }
}
