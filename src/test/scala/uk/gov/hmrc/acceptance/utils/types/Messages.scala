package uk.gov.hmrc.acceptance.utils.types

import play.api.libs.json.{JsObject, Json, OFormat}

object Messages {
  implicit val messagesJsonFormat: OFormat[Messages] = Json.format[Messages]
}

case class Messages(en: JsObject, cy: Option[JsObject])
