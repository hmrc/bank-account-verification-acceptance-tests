package uk.gov.hmrc.acceptance.stubs.transunion

import uk.gov.hmrc.acceptance.utils.types.Address

class IdentityCheckBuilder {

  private var currentAddressMatched: Address = Address(List("1", "Buck House"), town = Some("London"), postcode = Some("SW1A 1AA"))
  private var nameMatched = ""

  def currentAddressMatched(currentAddressMatched: Address): IdentityCheckBuilder = {
    this.currentAddressMatched = currentAddressMatched
    this
  }

  def nameMatched(nameMatched: String): IdentityCheckBuilder = {
    this.nameMatched = nameMatched
    this
  }

  def build(): String = {
    s"""<IdentityCheck>
       |  <addresspicklistfound>false</addresspicklistfound>
       |  <appverified>Yes</appverified>
       |  <cifas/>
       |  <confirmatorydobs>0</confirmatorydobs>
       |  <currentaddressmatched>${currentAddressMatched.asStringWithCommas()}</currentaddressmatched>
       |  <dvlawarning>false</dvlawarning>
       |  <ervalid>1</ervalid>
       |  <grodeceased>false</grodeceased>
       |  <halomatch>false</halomatch>
       |  <IDBasic/>
       |  <levelofconfidencebai>0</levelofconfidencebai>
       |  <levelofconfidenceccjs>0</levelofconfidenceccjs>
       |  <levelofconfidencedob>0</levelofconfidencedob>
       |  <levelofconfidenceer>3</levelofconfidenceer>
       |  <levelofconfidenceinvestors>0</levelofconfidenceinvestors>
       |  <levelofconfidenceshare>5</levelofconfidenceshare>
       |  <lorwarning>false</lorwarning>
       |  <matchlevel>IndividualReport</matchlevel>
       |  <namematched>$nameMatched</namematched>
       |  <namepicklistfound>false</namepicklistfound>
       |  <numaddresslinks>0</numaddresslinks>
       |  <numbais>0</numbais>
       |  <numccjs>0</numccjs>
       |  <numcorroborativechecks>4</numcorroborativechecks>
       |  <numcorroborativeotheridsconfirmed>0</numcorroborativeotheridsconfirmed>
       |  <numinvestors>0</numinvestors>
       |  <numprimarychecks>14</numprimarychecks>
       |  <numprimaryotheridsconfirmed>0</numprimaryotheridsconfirmed>
       |  <numsharerecords>16</numsharerecords>
       |  <pafvalid>true</pafvalid>
       |  <passportwarning>false</passportwarning>
       |  <readmatch>false</readmatch>
       |  <totaldobs>13</totaldobs>
       |</IdentityCheck>""".stripMargin
  }
}
