package uk.gov.hmrc.acceptance.spec.v2

import uk.gov.hmrc.acceptance.spec.BaseSpec

class InitSpec extends BaseSpec {

  Scenario("Cannot initialize a new journey with an unknown user agent") {

    val thrown = intercept[Exception] {
      initializeJourneyV2(userAgent = "unknown")
    }

    assert(thrown.getMessage === "Unable to initialize a new journey!")
  }
}
