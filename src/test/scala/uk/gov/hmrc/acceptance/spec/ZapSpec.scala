package uk.gov.hmrc.acceptance.spec

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.featurespec.AnyFeatureSpec
import uk.gov.hmrc.zap.ZapTest
import uk.gov.hmrc.zap.config.ZapConfiguration

class ZapSpec extends AnyFeatureSpec with ZapTest {
  val zapConfig: Config = ConfigFactory.load().getConfig("zap-automation-config")

  override val zapConfiguration: ZapConfiguration = new ZapConfiguration(zapConfig)

  Scenario("Run security checks") {
    triggerZapScan()
  }
}
