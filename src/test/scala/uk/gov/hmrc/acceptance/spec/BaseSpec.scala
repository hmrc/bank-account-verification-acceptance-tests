package uk.gov.hmrc.acceptance.spec

import java.nio.file.Paths

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, Outcome}
import uk.gov.hmrc.acceptance.utils.{BrowserDriver, JourneyBuilder}

trait BaseSpec extends AnyFeatureSpec
  with GivenWhenThen
  with BrowserDriver
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with JourneyBuilder
  with CommonAssertions
  with Matchers {

  override def beforeAll() {
    super.beforeAll()
    sys.addShutdownHook {
      webDriver.quit()
    }
  }

  override def afterEach: Unit = {
    webDriver.manage().deleteAllCookies()
  }

  override def afterAll() {
    webDriver.quit()
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
