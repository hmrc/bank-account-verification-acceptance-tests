package uk.gov.hmrc.acceptance.utils

import java.util.concurrent.TimeUnit.SECONDS

import io.findify.s3mock.S3Mock
import okhttp3.{MediaType, OkHttpClient, Request, RequestBody}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import uk.gov.hmrc.acceptance.config.TestConfig

trait SThreeMock extends AnyFeatureSpec
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  object BarsEndpoints {
    val REFRESH_EISCD_CACHE = "/refresh/cache/eiscd"
    val REFRESH_MODCHECK_CACHE = "/refresh/cache/modcheck"
  }

  private val s3Mock = S3Mock(port = TestConfig.s3MockPort(), dir = getClass.getResource("/sThreeBucket").getPath)
  private val okHttpClient: OkHttpClient = new OkHttpClient().newBuilder()
    .connectTimeout(10L, SECONDS)
    .readTimeout(10L, SECONDS)
    .build()


  override def beforeAll() {
    super.beforeAll()
    s3Mock.start
    initializeEISCDCache()
    initializeModcheckCache()
  }

  override def afterAll() {
    s3Mock.shutdown
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

  def initializeModcheckCache(): Unit = {
    val request = new Request.Builder()
      .url(s"${TestConfig.apiUrl("bank-account-reputation")}${BarsEndpoints.REFRESH_MODCHECK_CACHE}")
      .method("POST", RequestBody.create(MediaType.parse("application/json"), ""))
    val response = okHttpClient.newCall(request.build()).execute()
    if (!response.isSuccessful) {
      throw new IllegalStateException("Unable to initialize Modcheck Cache")
    }
  }
}
