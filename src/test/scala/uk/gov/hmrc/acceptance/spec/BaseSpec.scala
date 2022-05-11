/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.acceptance.spec

import io.findify.s3mock.S3Mock
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen, Outcome}
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.utils.{BrowserDriver, CommonActions, CommonAssertions, JourneyBuilder}

import java.nio.file.Paths

trait BaseSpec
    extends AnyFeatureSpec
    with GivenWhenThen
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with JourneyBuilder
    with BrowserDriver
    with CommonAssertions
    with CommonActions
    with Matchers {

  val s3Mock: S3Mock = new S3Mock.Builder()
    .withPort(TestConfig.s3MockPort())
    .withFileBackend(getClass.getResource("/sThreeBucket").getPath)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    s3Mock.start
    sys.addShutdownHook {
      webDriver.quit()
    }
    initializeEISCDCache()
    initializeModCheckCache()
  }

  override def afterEach: Unit =
    webDriver.manage().deleteAllCookies()

  override def afterAll(): Unit = {
    webDriver.quit()
    s3Mock.stop
  }

  override def withFixture(test: NoArgTest): Outcome = {
    val fixture = super.withFixture(test)
    if (!fixture.isSucceeded) {
      val screenshotDirectory = Paths.get("./target/screenshots").toAbsolutePath.toString
      val screenshotFilename  = test.name.replaceAll("\\s", "_").replaceAll("/", "")
      setCaptureDir(screenshotDirectory)
      capture to screenshotFilename
      println(s"Saved screenshot for failing test to '$screenshotDirectory/$screenshotFilename'")
    }
    fixture
  }
}
