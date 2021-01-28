package uk.gov.hmrc.acceptance.models.auth

import play.api.libs.json._

import scala.util.Try

sealed trait AffinityGroup

object AffinityGroup {

  case object Individual extends AffinityGroup

  case object Organisation extends AffinityGroup

  case object Agent extends AffinityGroup

  def isValid(name: String): Boolean = Try(AffinityGroup(name)).isSuccess

  def apply(name: String): AffinityGroup = {
    name match {
      case "Individual" => Individual
      case "Organisation" => Organisation
      case "Agent" => Agent
      case _ => throw new Exception(s"Invalid affinity group: $name")
    }
  }

  implicit val reads: Reads[AffinityGroup] = Reads[AffinityGroup] { json =>
    val nameString = json.as[String]
    if (AffinityGroup.isValid(nameString)) JsSuccess(AffinityGroup(nameString))
    else JsError(s"Unsupported AffinityGroup: $nameString")
  }
  implicit val writes: Writes[AffinityGroup] = Writes[AffinityGroup] { affinityGroup => JsString(affinityGroup.toString) }
}

