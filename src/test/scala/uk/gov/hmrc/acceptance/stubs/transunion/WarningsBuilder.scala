package uk.gov.hmrc.acceptance.stubs.transunion

class WarningsBuilder {

  private var nonGBRCardWarning = false
  private var namePicklistWarning = false
  private var addressPicklistWarning = false
  private var pafNonValidWarning = false
  private var cardAccountClosedWarning = false
  private var bankAccountClosedWarning = false

  def nonGBRCardWarning(nonGBRCardWarning: Boolean): WarningsBuilder = {
    this.nonGBRCardWarning = nonGBRCardWarning
    this
  }

  def namePicklistWarning(namePicklistWarning: Boolean): WarningsBuilder = {
    this.namePicklistWarning = namePicklistWarning
    this
  }

  def addressPicklistWarning(addressPicklistWarning: Boolean): WarningsBuilder = {
    this.addressPicklistWarning = addressPicklistWarning
    this
  }

  def pafNonValidWarning(pafNonValidWarning: Boolean): WarningsBuilder = {
    this.pafNonValidWarning = pafNonValidWarning
    this
  }

  def cardAccountClosedWarning(cardAccountClosedWarning: Boolean): WarningsBuilder = {
    this.cardAccountClosedWarning = cardAccountClosedWarning
    this
  }

  def bankAccountClosedWarning(bankAccountClosedWarning: Boolean): WarningsBuilder = {
    this.bankAccountClosedWarning = bankAccountClosedWarning
    this
  }

  def build(): String = {
    s"""<Warnings>
       |  <NonGBRCardWarning>$nonGBRCardWarning</NonGBRCardWarning>
       |  <NamePicklistWarning>$namePicklistWarning</NamePicklistWarning>
       |  <AddressPicklistWarning>$addressPicklistWarning</AddressPicklistWarning>
       |  <PAFNonValidWarning>$pafNonValidWarning</PAFNonValidWarning>
       |  <CardAccountClosedWarning>$cardAccountClosedWarning</CardAccountClosedWarning>
       |  <BankAccountClosedWarning>$bankAccountClosedWarning</BankAccountClosedWarning>
       |</Warnings>""".stripMargin
  }
}
