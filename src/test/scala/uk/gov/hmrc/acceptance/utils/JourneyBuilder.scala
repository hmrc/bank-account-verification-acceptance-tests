package uk.gov.hmrc.acceptance.utils

import java.util.concurrent.TimeUnit.SECONDS

import okhttp3._
import play.api.libs.json._
import uk.gov.hmrc.acceptance.config.TestConfig

trait JourneyBuilder {

  val httpClient: OkHttpClient = new OkHttpClient().newBuilder()
    .connectTimeout(10L, SECONDS)
    .readTimeout(10L, SECONDS)
    .build()

  def initializeJourneyPage(configuration: String = "{}"): String = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/init")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), Json.toJson(configuration).asInstanceOf[JsString].value))
    val response = httpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      val journeyId: String = response.body().string()
      s"${TestConfig.url("bank-account-verification")}/start/$journeyId"
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }
}

