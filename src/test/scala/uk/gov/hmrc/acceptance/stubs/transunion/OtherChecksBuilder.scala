package uk.gov.hmrc.acceptance.stubs.transunion

class OtherChecksBuilder {

  private var identityResult = "Pass"
  private var identityScore = "65"

  def identityResult(identityResult: String): OtherChecksBuilder = {
    this.identityResult = identityResult
    this
  }

  def identityScore(identityScore: String): OtherChecksBuilder = {
    this.identityScore = identityScore
    this
  }

  def build(): String = {
    s"""<AgeVerify/>
       |<OtherChecks>
       |  <IdentityResult>$identityResult</IdentityResult>
       |  <IdentityScore>$identityScore</IdentityScore>
       |</OtherChecks>
       |<DeviceRisk/>
       |<Phone>
       |  <MobileRisk>
       |    <Standard/>
       |    <Live/>
       |    <Score/>
       |  </MobileRisk>
       |</Phone>""".stripMargin
  }
}
