package forms.generate

import play.api.data._
import play.api.data.Forms._
import java.sql.Date

case class GenerateForms(
  slickDriver: Option[String],
  jdbcDriver: Option[String],
  url: Option[String],
  user: Option[String],
  password: Option[String],
  schema: Option[String],
  outputFolder: String,
  pkg: Option[String],
  ignoreTables: Option[List[String]])

trait GenerateForm {

  val generateForm = Form(
    mapping(
      "slickDriver" -> optional(text(maxLength = 100)),
      "jdbcDriver" -> optional(text(maxLength = 100)),
      "url" -> optional(text(maxLength = 100)),
      "user" -> optional(text(maxLength = 100)),
      "password" -> optional(text(maxLength = 100)),
      "schema" -> optional(text(maxLength = 100)),
      "outputFolder" -> nonEmptyText(maxLength = 100),
      "pkg" -> optional(text(maxLength = 100)),
      "ignoreTables" -> optional(list(text(maxLength = 100))))(GenerateForms.apply)(GenerateForms.unapply))
}