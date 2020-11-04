package uk.gov.hmrc.acceptance.utils

import java.util.concurrent.TimeUnit.SECONDS

import okhttp3._
import play.api.libs.json._
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.{InitRequest, InitResponse}

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
  def initializeJourney(configuration: String = InitRequest.apply().asJsonString()): InitResponse = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-verification")}/init")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), Json.toJson(configuration).asInstanceOf[JsString].value))
    val response = okHttpClient.newCall(request.build()).execute()
    if (response.isSuccessful) {
      Json.parse(response.body.string()).as[InitResponse]
    } else {
      throw new IllegalStateException("Unable to initialize a new journey!")
    }
  }

  def journeyPage(startPath: String): String = {
    s"${TestConfig.getHost("bank-account-verification")}$startPath"
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

