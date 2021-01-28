package uk.gov.hmrc.acceptance.utils

import okhttp3._
import play.api.libs.json._
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models._
import uk.gov.hmrc.acceptance.models.auth.{AffinityGroup, ConfidenceLevel, CredId, CredentialRole, CredentialStrength, Enrolment}

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
  def initializeJourney(configuration: String = InitRequest.apply().asJsonString()): JourneyBuilderResponse = {
    // **NOTE** credId can only be a maximum of 30 characters, anything longer will result in a mismatch and no journey will be found
    val credId = randomUUID().toString.slice(0,29)
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

