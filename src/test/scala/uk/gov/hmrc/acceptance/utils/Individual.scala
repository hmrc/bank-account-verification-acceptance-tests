package uk.gov.hmrc.acceptance.utils

case class Individual(
                       title: Option[String] = None,
                       firstName: Option[String] = None,
                       middleNames: Option[String] = None,
                       lastName: Option[String] = None
                     ) {
  def asString(): String = {
    s"${title.getOrElse("")} ${firstName.getOrElse("")} ${middleNames.getOrElse("")} ${lastName.getOrElse("")}".trim.replaceAll(" +", " ")
  }

  def asEscapedString(): String = {
    this.asString().replaceAll("\'", "\\\\\'")
  }
}
