package uk.gov.hmrc.acceptance.spec

import java.nio.file.Paths

import io.findify.s3mock.S3Mock
import org.openqa.selenium.{MutableCapabilities, WebDriver}
import org.scalatest._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.selenium.WebBrowser
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.utils.JourneyBuilder
import uk.gov.hmrc.webdriver.SingletonDriver

trait BaseSpec extends AnyFeatureSpec
  with GivenWhenThen
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with JourneyBuilder
  with Matchers {

  val device: Option[String] = sys.props.get("device").map(_.toLowerCase)
  var options: Option[MutableCapabilities] = None
  implicit lazy val webDriver: WebDriver = SingletonDriver.getInstance(options)
  private val s3Mock = S3Mock(port = TestConfig.s3MockPort(), dir = getClass.getResource("/sThreeBucket").getPath)

  override def beforeAll() {
    super.beforeAll()
    s3Mock.start
    initializeEISCDCache()
    sys.addShutdownHook {
      webDriver.quit()
    }
  }

  override def afterEach: Unit = {
    webDriver.manage().deleteAllCookies()
  }

  override def afterAll() {
    webDriver.quit()
    s3Mock.shutdown
  }

  override def withFixture(test: NoArgTest): Outcome = {
    val fixture = super.withFixture(test)
    if (!fixture.isSucceeded) {
      val screenshotDirectory = Paths.get("./target/screenshots").toAbsolutePath.toString
      val screenshotFilename = test.name.replaceAll("\\s", "_").replaceAll("/", "")
      setCaptureDir(screenshotDirectory)
      capture to screenshotFilename
      println(s"Saved screenshot for failing test to '$screenshotDirectory/$screenshotFilename'")
    }
    fixture
  }
}
