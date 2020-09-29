package uk.gov.hmrc.acceptance.spec

import com.typesafe.config.{Config, ConfigFactory}
import uk.gov.hmrc.acceptance.tags.Zap
import uk.gov.hmrc.acceptance.utils.{BaseSpec, MockServer}
import uk.gov.hmrc.zap.ZapTest
import uk.gov.hmrc.zap.config.ZapConfiguration

class ZapSpec extends BaseSpec with MockServer with ZapTest {
  val zapConfig: Config = ConfigFactory.load().getConfig("zap-automation-config")

  override val zapConfiguration: ZapConfiguration = new ZapConfiguration(zapConfig)

  Scenario("Run security checks", Zap) {
    triggerZapScan()
  }
}

