package uk.gov.hmrc.acceptance.utils

import java.nio.file.Paths

import io.findify.s3mock.S3Mock
import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{By, MutableCapabilities, WebDriver, WebElement}
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, Outcome}
import org.scalatestplus.selenium.WebBrowser
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.webdriver.SingletonDriver

trait BaseSpec extends AnyFeatureSpec
  with GivenWhenThen
  with WebBrowser
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with JourneyBuilder
  with Matchers {

  val SUREPAY_PATH = "/surepay/v1/gateway"
  val TRANSUNION_PATH = "/callvalidateapi"
  val CREDITSAFE_PATH = "/Match"

  val device: Option[String] = sys.props.get("device").map(_.toLowerCase)
  var options: Option[MutableCapabilities] = None
  implicit lazy val webDriver: WebDriver = SingletonDriver.getInstance(options)
  lazy val webDriverWillWait: WebDriverWait = new WebDriverWait(webDriver, 5, 250)
  private val s3Mock = S3Mock(port = TestConfig.s3MockPort(), dir = getClass.getResource("/sThreeBucket").getPath)

  override def beforeAll() {
    super.beforeAll()
    s3Mock.start
    initializeEISCDCache()
    initializeModcheckCache()
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

  def assertThatErrorSummaryLinkExists(elementIdentifier: String): Unit = {
    webDriverWillWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".govuk-error-summary")))
    val errorSummary: WebElement = cssSelector(s"a[href*='$elementIdentifier']").findElement.get.underlying
    assertThat(errorSummary.isDisplayed).isTrue
    assertThat(errorSummary.getText).isNotEmpty
  }

  def assertThatInputFieldErrorMessageExists(elementIdentifier: String): Unit = {
    val dataEntryField = id(s"$elementIdentifier").findElement.get
    val errorBorderClass = "govuk-input--error"
    val errorMessageLocator = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement = webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
    assertThat(dataEntryField.attribute("class").get).contains(errorBorderClass)
  }

  def assertThatRadioButtonErrorMessageIsDisplayed(elementIdentifier: String): Unit = {
    val errorMessageLocator = By.id(s"$elementIdentifier-error")
    val errorMessage: WebElement = webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator))
    assertThat(errorMessage.isDisplayed).isTrue
    assertThat(errorMessage.getText).isNotEmpty
  }

  def errorMessageSummaryCount(): Int = {
    webDriverWillWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".govuk-error-summary__list")))
    cssSelector(".govuk-error-summary__list > li").findAllElements.length
  }
}
