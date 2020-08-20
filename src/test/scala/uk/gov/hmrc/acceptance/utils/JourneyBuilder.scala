package uk.gov.hmrc.acceptance.utils

import java.util.concurrent.TimeUnit.SECONDS

import okhttp3._
import play.api.libs.json._
import uk.gov.hmrc.acceptance.config.TestConfig

trait JourneyBuilder {

  val defaultConfiguration: String =
    s"""
       |{
       |    "continueUrl" : "${TestConfig.url("bank-account-verification-frontend-example")}/done",
       |    "serviceIdentifier" : "BAVF Acceptance Test"
       |}
       |""".stripMargin

  val httpClient: OkHttpClient = new OkHttpClient().newBuilder()
    .connectTimeout(10L, SECONDS)
    .readTimeout(10L, SECONDS)
    .build()

  def initializeJourney(configuration: String = defaultConfiguration): String = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/init")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), Json.toJson(configuration).asInstanceOf[JsString].value))
    val response = httpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      //TODO do this properly when we respond with the correct JSON block.
      response.body().string().replaceAll("\"", "")
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  def journeyStartPage(journeyId: String): String = {
    s"${TestConfig.url("bank-account-verification")}/start/$journeyId"
  }

  def initializeEISCDCache(): Unit = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-reputation")}/refresh/eiscdcache")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), ""))
    val response = httpClient.newCall(request.build()).execute()
    if (!response.isSuccessful) {
      throw new IllegalStateException("Unable to initialize EISCD Cache")
    }
  }
}

