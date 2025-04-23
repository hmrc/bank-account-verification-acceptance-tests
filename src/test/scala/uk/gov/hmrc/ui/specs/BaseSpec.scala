/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ui.specs

import com.adobe.testing.s3mock.S3MockApplication
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}
import uk.gov.hmrc.selenium.webdriver.{Browser, ScreenshotOnFailure}
import uk.gov.hmrc.ui.config.TestConfig
import uk.gov.hmrc.ui.utils.{BrowserDriver, CommonActions, CommonAssertions, JourneyBuilder}

import java.io.File

trait BaseSpec
    extends AnyFeatureSpec
    with GivenWhenThen
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with BrowserDriver
    with Browser
    with CommonAssertions
    with CommonActions
    with Matchers
    with ScreenshotOnFailure {

  val journeyBuilder = new JourneyBuilder

  val s3Dir = new File(getClass.getResource("/sThreeBucket").getFile)

  var s3MockApp: Option[S3MockApplication] = None

  override def beforeAll(): Unit = {
    super.beforeAll()

    s3MockApp = Some(
      S3MockApplication.start(f"--server.port=${TestConfig.s3MockPort()}", s"--root.directory=${s3Dir.getAbsolutePath}")
    )

    startBrowser()

    sys.addShutdownHook {
      quitBrowser()
    }

    journeyBuilder.initializeEISCDCache()
    journeyBuilder.initializeModCheckCache()
  }

  override def afterEach: Unit =
    webDriver.manage().deleteAllCookies()

  override def afterAll(): Unit =
    if (s3MockApp.isDefined) {
      s3MockApp.get.stop()
      s3MockApp = None
    }

  quitBrowser()

}
