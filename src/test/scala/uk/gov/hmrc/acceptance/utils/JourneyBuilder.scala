package uk.gov.hmrc.acceptance.utils

import java.util.concurrent.TimeUnit.SECONDS

import okhttp3._
import play.api.libs.json._
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.utils.types.InitJourney

trait JourneyBuilder {

  val okHttpClient: OkHttpClient = new OkHttpClient().newBuilder()
    .connectTimeout(10L, SECONDS)
    .readTimeout(10L, SECONDS)
    .build()

  //TODO set a "serviceIdentifier" with whitespace to check it returns with a 400 when #TAV-101 is complete?
  def initializeJourney(configuration: String = InitJourney.apply().asJsonString()): String = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/init")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), Json.toJson(configuration).asInstanceOf[JsString].value))
    val response = okHttpClient.newCall(request.build()).execute()
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
    val response = okHttpClient.newCall(request.build()).execute()
    if (!response.isSuccessful) {
      throw new IllegalStateException("Unable to initialize EISCD Cache")
    }
  }
}

