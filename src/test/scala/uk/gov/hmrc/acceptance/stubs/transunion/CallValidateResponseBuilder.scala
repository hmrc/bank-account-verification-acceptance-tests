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

package uk.gov.hmrc.acceptance.stubs.transunion

import uk.gov.hmrc.acceptance.models.{Address, Individual}

class CallValidateResponseBuilder() {

  private var individual: Individual        =
    Individual(title = Some("Mrs"), firstName = Some("Annie"), lastName = Some("Mclaren"))
  private var address: Address              = Address(List("1", "Buck House"), town = Some("London"), postcode = Some("SW1A 1AA"))
  private var dateOfBirth                   = "1970-10-01"
  private var bankCheckEnhanced: String     = new BankCheckEnhancedBuilder().build()
  private var bankEnhancedCompleted         = "yes"
  private var identityCheck: String         = new IdentityCheckBuilder()
    .nameMatched(individual.asString())
    .build()
  private var otherChecks: String           = new OtherChecksBuilder().build()
  private var warnings: String              = new WarningsBuilder().build()
  private var additionalChecksFlag: Boolean = true
  private var errors                        = ""

  def setInputIndividualData(individual: Individual): CallValidateResponseBuilder = {
    this.individual = individual
    this
  }

  def setInputAddress(address: Address): CallValidateResponseBuilder = {
    this.address = address
    this
  }

  def setInputdateOfBirth(dateOfBirth: String): CallValidateResponseBuilder = {
    this.dateOfBirth = dateOfBirth
    this
  }

  def bankCheckEnhanced(bankCheckEnhanced: String): CallValidateResponseBuilder = {
    this.bankCheckEnhanced = bankCheckEnhanced
    this
  }

  def identityCheck(identityCheck: String): CallValidateResponseBuilder = {
    this.identityCheck = identityCheck
    this
  }

  def otherChecks(otherChecks: String): CallValidateResponseBuilder = {
    this.otherChecks = otherChecks
    this
  }

  def warnings(warnings: String): CallValidateResponseBuilder = {
    this.warnings = warnings
    this
  }

  def withError(error: String): CallValidateResponseBuilder = {
    this.errors = s"""
         |<Errors>
         |    <Error>$error</Error>
         |</Errors>""".stripMargin
    this.additionalChecksFlag = false
    this.bankEnhancedCompleted = "no"
    this
  }

  private def additionalChecks(): String =
    if (additionalChecksFlag) {
      s"""
         |$bankCheckEnhanced
         |$identityCheck
         |$otherChecks
         |$warnings""".stripMargin
    } else {
      ""
    }

  def build(): String =
    scala.xml.XML
      .loadString(
        s"""<Results xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" APIVERSION="5.7.0 - 20150811">
         |  <Result RID="CC-TEST-HARNESS" PID="LTJ-CT1-8871-46651-5788" DateTime="21-06-2016 14:29">
         |    <Displays>
         |      <ChecksCompleted>
         |        <BankStandard>yes</BankStandard>
         |        <BankEnhanced>$bankEnhancedCompleted</BankEnhanced>
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
         |          <Title>${individual.title.getOrElse("undefined")}</Title>
         |          <Firstname>${individual.firstName.getOrElse("")}</Firstname>
         |          <Surname>${individual.lastName.getOrElse("")}</Surname>
         |        </Individual>
         |        <Address>
         |          <Buildingnumber>${address.lineOne()}</Buildingnumber>
         |          <Postcode>${address.postcode.getOrElse("")}</Postcode>
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
         |      </BankcheckStandard>${additionalChecks()}$errors
         |    </Displays>
         |  </Result>
         |</Results>
         |  """.stripMargin
      )
      .toString()

}
