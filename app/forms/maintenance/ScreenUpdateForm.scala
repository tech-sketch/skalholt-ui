package forms.maintenance

import play.api.data._
import play.api.data.Forms._
import scala.annotation.meta.field
import utils.annotations.searchcondition.{ Eq => ==, Ne => !=, Lt => <, Le => <=, Gt => >, Ge => >=, Contains => contains, StartWith => startwith, EndWith => endwith, In => in }

case class ScreenUpdateForms(
  @(contains @field) screenId: String,
  @(contains @field) screenNm: Option[String],
  @(contains @field) subsystemNmEn: Option[String],
  @(== @field) screenType: Option[String],
  @(contains @field) entityNmEn: Option[String]
  )
trait ScreenUpdateForm {

  val screenUpdateForm = Form(
    mapping(
      "screenId" -> text(maxLength = 50),
      "screenNm" -> optional(text(maxLength = 100)),
      "subsystemNmEn" -> optional(text(maxLength = 100)),
      "screenType" -> optional(text),
      "entityNmEn" -> optional(text(maxLength = 50))
      )(ScreenUpdateForms.apply)(ScreenUpdateForms.unapply))
}