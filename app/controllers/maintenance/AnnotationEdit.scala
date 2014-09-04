package controllers.maintenance

import controllers.common.CommonController
import models.db.generator._
import models.Tables._
import forms.maintenance.{ AnnotationEditForm, AnnotationForms, Anno }
import play.api.db._
import play.api.mvc._
import play.api.db.slick._
import scala.collection.JavaConversions._
import scala.util.control.Breaks

object AnnotationEdit extends CommonController with AnnotationEditForm {
  /** Select */
  def select(rowId: String, screenId: String, itemNo: String) = DBAction.transaction { implicit request =>
    val domainCd = screenId + "-" + itemNo
    val domainCdFind = AnnotationDefinitions.joinAnnotationAndDefinition(domainCd)
    Ok(views.html.maintenance.annotationEdit(annotationForm,domainCdFind,rowId))

  }

}

