package uk.gov.hmrc.acceptance.spec

import java.nio.file.Paths

import org.openqa.selenium.{MutableCapabilities, WebDriver}
import org.scalatest._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.selenium.WebBrowser
import uk.gov.hmrc.acceptance.utils.JourneyBuilder
import uk.gov.hmrc.webdriver.SingletonDriver

abstract class SpecBase extends AnyFeatureSpec
  with GivenWhenThen
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with JourneyBuilder
  with Matchers {

  val device: Option[String] = sys.props.get("device").map(_.toLowerCase)
  var options: Option[MutableCapabilities] = None
  implicit lazy val webDriver: WebDriver = SingletonDriver.getInstance(options)

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
