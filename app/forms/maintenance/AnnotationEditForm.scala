package forms.maintenance

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.RequestHeader

case class AnnotationForms(annotationCd: String, annotationNmEn: Option[String], argumentType: Option[String], argument1: Option[String], argument2: Option[String], argument3: Option[String], classpathStr: Option[String])
case class Anno(var annotation: List[AnnotationForms] = List.empty) {}

trait AnnotationEditForm {

  def annotationForm(implicit request: RequestHeader): Form[Anno] = Form(
    mapping(
      "annotation" -> play.api.data.Forms.list(mapping(
        "annotationCd" -> nonEmptyText,
        "annotationNmEn" -> optional(nonEmptyText),
        "argumentType" -> optional(nonEmptyText),
        "argument1" -> optional(nonEmptyText),
        "argument2" -> optional(nonEmptyText),
        "argument3" -> optional(nonEmptyText),
        "classpathStr" -> optional(nonEmptyText))(AnnotationForms.apply)(AnnotationForms.unapply)))(Anno.apply)(Anno.unapply))

}

