package uk.gov.hmrc.acceptance.models

import play.api.libs.json.{Json, OFormat}

object Address {
  implicit val addressJsonFormat: OFormat[Address] = Json.format[Address]
}

case class Address(lines: List[String], town: Option[String] = None, postcode: Option[String] = None) {

  def lineOne(): String = {
    lines.head
  }

  def asStringWithCR(): String = {
    val addressAsString = new StringBuilder
    for (entry <- lines.indices) {
      if (entry == 0) {
        addressAsString.append(lines(entry))
      } else {
        addressAsString.append(s"\n${lines(entry)}")
      }
    }
    addressAsString
      .append(insertPrefix(town, "\n"))
      .append(insertPrefix(postcode, "\n"))
      .mkString
  }

  def asStringWithCommas(): String = {
    val addressAsString = new StringBuilder
    for (entry <- lines.indices) {
      if (entry == 0) {
        addressAsString.append(lines(entry))
      } else {
        addressAsString.append(s", ${lines(entry)}")
      }
    }
    addressAsString
      .append(insertPrefix(town, ", "))
      .append(insertPrefix(postcode, ", "))
      .mkString
  }

  def insertPrefix(item: Option[String], character: String): String = {
    item match {
      case Some(value) => s"$character$value"
      case None => ""
    }
  }
}
