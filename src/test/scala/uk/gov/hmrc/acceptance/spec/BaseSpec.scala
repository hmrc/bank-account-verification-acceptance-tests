package uk.gov.hmrc.acceptance.spec

import java.nio.file.Paths

import io.findify.s3mock.S3Mock
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, Outcome}
import uk.gov.hmrc.acceptance.utils.{BrowserDriver, JourneyBuilder}

trait BaseSpec extends AnyFeatureSpec
  with GivenWhenThen
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with JourneyBuilder
  with BrowserDriver
  with CommonAssertions
  with Matchers {

  val s3Mock: S3Mock = new S3Mock.Builder().withPort(8001).withFileBackend(getClass.getResource("/sThreeBucket").getPath).build()

  override def beforeAll() {
    super.beforeAll()
    s3Mock.start
    sys.addShutdownHook {
      webDriver.quit()
    }
    initializeEISCDCache()
    initializeModCheckCache()
  }

  override def afterEach: Unit = {
    webDriver.manage().deleteAllCookies()
  }

  override def afterAll() {
    webDriver.quit()
    s3Mock.stop
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
