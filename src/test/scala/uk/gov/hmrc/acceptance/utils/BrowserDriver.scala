package uk.gov.hmrc.acceptance.utils

import com.typesafe.scalalogging.LazyLogging
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.{MutableCapabilities, WebDriver}
import org.scalatestplus.selenium.WebBrowser
import uk.gov.hmrc.webdriver.SingletonDriver

trait BrowserDriver extends WebBrowser with LazyLogging {
  logger.info(s"Instantiating Browser: ${sys.props.getOrElse("browser", "'browser' System property not set. This is required")}")

  val device: Option[String] = sys.props.get("device").map(_.toLowerCase)
  var options: Option[MutableCapabilities] = None
  implicit lazy val webDriver: WebDriver = SingletonDriver.getInstance(options)
  lazy val webDriverWillWait: WebDriverWait = new WebDriverWait(webDriver, 5, 250)
}
