package uk.gov.hmrc.acceptance.spec

import java.nio.file.Paths

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.featurespec.AnyFeatureSpec
import play.api.libs.json.Json
import uk.gov.hmrc.acceptance.config.TestConfig
import uk.gov.hmrc.acceptance.models.UrlList
import uk.gov.hmrc.acceptance.tags.Excluded
import uk.gov.hmrc.acceptance.utils.JourneyBuilder
import uk.gov.hmrc.zap.ZapTest
import uk.gov.hmrc.zap.config.ZapConfiguration

class ZapSpec extends AnyFeatureSpec with ZapTest with JourneyBuilder {
  lazy val zapConfig: Config = ConfigFactory.load().getConfig("zap-automation-config")
  override lazy val zapConfiguration: ZapConfiguration = new ZapConfiguration(zapConfig)

  Scenario("Run security checks", Excluded) {
    val personalJourneyId = getJourneyIdForUrlPath(".*/personal/.*")
    val businessJourneyId = getJourneyIdForUrlPath(".*/business/.*")
    // start is called to initiate the journey, so a personal JourneyId is also a valid start JourneyId
    val excluded = Array(
      s"${TestConfig.url("bank-account-verification")}/start/(?!$personalJourneyId).*",
      s"${TestConfig.url("bank-account-verification")}/verify/personal/(?!$personalJourneyId).*",
      s"${TestConfig.url("bank-account-verification")}/confirm/personal/(?!$personalJourneyId).*",
      s"${TestConfig.url("bank-account-verification")}/verify/business/(?!$businessJourneyId).*",
      s"${TestConfig.url("bank-account-verification")}/confirm/business/(?!$businessJourneyId).*"
    )

    defineExcludedURLs(excluded)
    triggerZapScan()
  }

  def getJourneyIdForUrlPath(regex: String): String = {
    val matchingUrlsFoundByZap = zapClient.callZapApi(
      "/json/search/view/urlsByUrlRegex",
      "regex" -> regex,
      "baseurl" -> TestConfig.url("bank-account-verification"),
      "start" -> "1",
      "count" -> "1"
    )
    val urlList = Json.parse(matchingUrlsFoundByZap).as[UrlList]
    val singleUrl = Paths.get(urlList.urlsByUrlRegex.head.url)
    singleUrl.getName(singleUrl.getNameCount - 1).toString
  }

  def defineExcludedURLs(excluded: Array[String]): Unit = {
    zapClient.callZapApi(
      "/json/context/action/setContextRegexs",
      "contextName" -> zapContext.name,
      "incRegexs" -> Array(zapConfiguration.contextBaseUrlRegex).mkString("[\"", "\", \"", "\"]"),
      "excRegexs" -> excluded.mkString("[\"", "\", \"", "\"]")
    )
  }
}
