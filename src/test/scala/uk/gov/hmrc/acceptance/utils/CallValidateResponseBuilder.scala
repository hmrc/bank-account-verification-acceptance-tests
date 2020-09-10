package uk.gov.hmrc.acceptance.utils

class CallValidateResponseBuilder {

  var title = "Mrs"
  var firstName = "ANNIE"
  var surname = "MCLAREN"
  var dateOfBirth = "1970-01-01"
  var errors = ""
  var bankEnhancedCompleted = "yes"
  var BankCheckEnhanced: String =
    s"""<BankcheckEnhanced>
       |  <Result>Pass</Result>
       |  <Score>7</Score>
       |  <AccountIssuer>HSBC BANK PLC</AccountIssuer>
       |  <OtherAccountsFoundForIssuer>yes</OtherAccountsFoundForIssuer>
       |  <AccountStartDate>1999-10-13</AccountStartDate>
       |</BankcheckEnhanced>""".stripMargin
  var identityCheck: String =
    s"""<IdentityCheck>
       |  <addresspicklistfound>false</addresspicklistfound>
       |  <appverified>Yes</appverified>
       |  <cifas/>
       |  <confirmatorydobs>0</confirmatorydobs>
       |  <currentaddressmatched>1, BUCK HOUSE, LONDON, SW1A 1AA</currentaddressmatched>
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
       |  <namematched>MR DAVID ANTHONY TEST</namematched>
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
  var otherChecks: String =
    s"""<AgeVerify/>
       |<OtherChecks>
       |  <IdentityResult>Pass</IdentityResult>
       |  <IdentityScore>65</IdentityScore>
       |</OtherChecks>
       |<DeviceRisk/>
       |<Phone>
       |  <MobileRisk>
       |    <Standard/>
       |    <Live/>
       |    <Score/>
       |  </MobileRisk>
       |</Phone>""".stripMargin
  var warnings: String =
    s"""<Warnings>
       |  <NonGBRCardWarning>false</NonGBRCardWarning>
       |  <NamePicklistWarning>false</NamePicklistWarning>
       |  <AddressPicklistWarning>false</AddressPicklistWarning>
       |  <PAFNonValidWarning>false</PAFNonValidWarning>
       |  <CardAccountClosedWarning>false</CardAccountClosedWarning>
       |  <BankAccountClosedWarning>false</BankAccountClosedWarning>
       |</Warnings>""".stripMargin

  var additionalChecks: String =
    s"""
       |$BankCheckEnhanced
       |$identityCheck
       |$otherChecks
       |$warnings""".stripMargin

  def setIndividualData(title: String, firstName: String, surname: String, dateOfBirth: String): CallValidateResponseBuilder = {
    this.title = title
    this.firstName = firstName
    this.surname = surname
    this.dateOfBirth = dateOfBirth
    this
  }

  def withError(error: String): CallValidateResponseBuilder = {
    this.errors =
      s"""
         |<Errors>
         |    <Error>$error</Error>
         |</Errors>""".stripMargin
    this.additionalChecks = ""
    this.bankEnhancedCompleted = "no"
    this
  }


  def build(): String = {
    scala.xml.XML.loadString(
      s"""<Results xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" APIVERSION="5.7.0 - 20150811">
         |  <Result RID="CC-TEST-HARNESS" PID="LTJ-CT1-8871-46651-5788" DateTime="21-06-2016 14:29">
         |    <Displays>
         |      <ChecksCompleted>
         |        <BankStandard>$bankEnhancedCompleted</BankStandard>
         |        <BankEnhanced>yes</BankEnhanced>
         |        <CardLive>no</CardLive>
         |        <CardEnhanced>no</CardEnhanced>
         |        <IDEnhanced>yes</IDEnhanced>
         |        <NCOAAlert>no</NCOAAlert>
         |        <CallValidate3D>no</CallValidate3D>
         |        <TheAffordabilityReport>no</TheAffordabilityReport>
         |        <DeliveryFraud>no</DeliveryFraud>
         |        <CreditScore>no</CreditScore>
         |        <Zodiac>no</Zodiac>
         |        <BankAccountPlus>no</BankAccountPlus>
         |        <BankOFA>no</BankOFA>
         |        <CardOFA>no</CardOFA>
         |        <RealTimeFraudAlerts>no</RealTimeFraudAlerts>
         |        <DeviceRisk>no</DeviceRisk>
         |        <MobileRisk>no</MobileRisk>
         |      </ChecksCompleted>
         |      <InputData>
         |        <Individual>
         |          <Dateofbirth>$dateOfBirth</Dateofbirth>
         |          <Title>$title</Title>
         |          <Firstname>$firstName</Firstname>
         |          <Surname>$surname</Surname>
         |        </Individual>
         |        <Address>
         |          <Buildingnumber>1</Buildingnumber>
         |          <Postcode>X9 9AB</Postcode>
         |        </Address>
         |      </InputData>
         |      <BankcheckStandard>
         |        <Date_of_last_change>06/06/2013</Date_of_last_change>
         |        <BACS_Status>M</BACS_Status>
         |        <BACS_Settlement_Bank>0005</BACS_Settlement_Bank>
         |        <Settlement_section>01</Settlement_section>
         |        <Settlement_Subsection>11</Settlement_Subsection>
         |        <Handling_Bank>0005</Handling_Bank>
         |        <Account_numbered_flag>Y</Account_numbered_flag>
         |        <DDI_voucher_flag>N</DDI_voucher_flag>
         |        <CHAPS_Effective_date_of_last_change>09/07/2001</CHAPS_Effective_date_of_last_change>
         |        <CCCC_Settlement_bank>5</CCCC_Settlement_bank>
         |        <GB_Northern_Ireland_Indicator>GB</GB_Northern_Ireland_Indicator>
         |        <Branch_type_indicator>M</Branch_type_indicator>
         |        <Branch_Name_or_Place>Morpeth1</Branch_Name_or_Place>
         |        <Full_Branch_title_part_1>Morpeth2</Full_Branch_title_part_1>
         |        <Address_line_1>1 New Market</Address_line_1>
         |        <Address_Town>Morpeth3</Address_Town>
         |        <Post_Code_major_part>NE61</Post_Code_major_part>
         |        <Post_Code_minor_part>1PX</Post_Code_minor_part>
         |        <Supervisory_Body>A</Supervisory_Body>
         |        <Bank_Code_of_owning_bank>0005</Bank_Code_of_owning_bank>
         |        <Full_name_of_owning_bank_line_1>HSBC BANK PLC</Full_name_of_owning_bank_line_1>
         |        <Short_name_of_owning_bank>HSBC</Short_name_of_owning_bank>
         |        <Short_Branch_title>Morpeth4</Short_Branch_title>
         |        <Sub_Branch_suffix>00</Sub_Branch_suffix>
         |        <BIC_branch>18H</BIC_branch>
         |        <BIC_bank>MIDLGB21</BIC_bank>
         |        <sortcode>999998</sortcode>
         |        <CCCC_Effective_date_of_last_change>14/10/1983</CCCC_Effective_date_of_last_change>
         |        <CCCC_Status>M</CCCC_Status>
         |        <Date_closed_in_CHAPS_Euro_clearing>23/09/2008</Date_closed_in_CHAPS_Euro_clearing>
         |        <CHAPSE_Status>N</CHAPSE_Status>
         |        <CHAPS_routing_BIC_branch>XXX</CHAPS_routing_BIC_branch>
         |        <CHAPS_routing_BIC_bank>MIDLGB22</CHAPS_routing_BIC_bank>
         |        <CHAPS_settlement_member>005</CHAPS_settlement_member>
         |        <CHAPS_Status>I</CHAPS_Status>
         |        <BACS_Date_of_last_change>06/06/2005</BACS_Date_of_last_change>
         |        <FPS_Status>M</FPS_Status>
         |        <FPS_DateLastChanged>23/02/2008</FPS_DateLastChanged>
         |        <FPS_SettlementBankConnection>01</FPS_SettlementBankConnection>
         |        <FPS_SettlementBankCode>0005</FPS_SettlementBankCode>
         |        <FPS_HandlingBankConnection>01</FPS_HandlingBankConnection>
         |        <FPS_HandlingBankCode>0005</FPS_HandlingBankCode>
         |        <FPS_AccountNumbersFlag>Y</FPS_AccountNumbersFlag>
         |      </BankcheckStandard>$additionalChecks$errors
         |    </Displays>
         |  </Result>
         |</Results>
         |  """.stripMargin
    ).toString()
  }

}
