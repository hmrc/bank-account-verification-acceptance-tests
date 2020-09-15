package uk.gov.hmrc.acceptance.stubs.transunion

class BankCheckEnhancedBuilder {

  private var result = "Pass"
  private var score = "7"
  private var accountIssuer = "HSBC BANK PLC"
  private var otherAccountsFound = "yes"
  private var accountStartDate = "1999-10-13"

  def result(result: String): BankCheckEnhancedBuilder = {
    this.result = result
    this
  }

  def score(score: String): BankCheckEnhancedBuilder = {
    this.score = score
    this
  }

  def accountIssuer(accountIssuer: String): BankCheckEnhancedBuilder = {
    this.accountIssuer = accountIssuer
    this
  }

  def otherAccountsFound(otherAccountsFound: String): BankCheckEnhancedBuilder = {
    this.otherAccountsFound = otherAccountsFound
    this
  }

  def accountStartDate(accountStartDate: String): BankCheckEnhancedBuilder = {
    this.accountStartDate = accountStartDate
    this
  }

  def build(): String = {
    s"""<BankcheckEnhanced>
       |  <Result>$result</Result>
       |  <Score>$score</Score>
       |  <AccountIssuer>$accountIssuer</AccountIssuer>
       |  <OtherAccountsFoundForIssuer>$otherAccountsFound</OtherAccountsFoundForIssuer>
       |  <AccountStartDate>$accountStartDate</AccountStartDate>
       |</BankcheckEnhanced>""".stripMargin
  }
}
