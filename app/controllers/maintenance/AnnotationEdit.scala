package controllers.maintenance

import controllers.common.CommonController
import daos.AnnotationDefinitions
import forms.maintenance.{AnnotationEditForm}
import play.api.mvc._
import scala.concurrent.duration.Duration
import play.cache.Cache
import skalholt.codegen.constants.GenConstants.GenParam
import skalholt.codegen.database.common.DBUtils
import skalholt.codegen.util.StringUtil._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class AnnotationEdit extends CommonController with AnnotationEditForm {
  /** Select */
  def select(rowId: String, screenId: String, entityNm: String, itemNo: String, insideItemNmEn: String) = Action.async { implicit request =>

    val annotationType: String = Cache.get("genparam") match {
      case p: GenParam =>
        try {
          DBUtils.getModel(p.bizJdbcDriver.get, p.bizUrl.get, p.bizSchema.get, p.bizUser, p.bizPassword)
            .tables.filter(t => decapitalize(camelize(t.name.table)) == entityNm)
            .head.columns.filter(n => decapitalize(camelize(n.name)) == insideItemNmEn)
            .map(m => m.tpe).head

        } catch {
          case e: Exception => ("-- Tables is not found.")
        }
      case _ => ("-- Tables is not found.")
    }

    val domainCd = screenId + "-" + itemNo
    val annotationJoin = Await.result(AnnotationDefinitions.joinAnnotationAndDefinition(domainCd), Duration.Inf)
    val annotationList = (annotationType match {
      case "Int" =>
        annotationJoin.filter(t => t._1.annotationCd == "number")
      case "String" =>
        annotationJoin.filter(t => t._1.annotationCd == "text")
      case "java.sql.Date" =>
        annotationJoin.filter(t => t._1.annotationCd == "sqlDate") ++ annotationJoin.filter(t => t._1.annotationCd == "sqlTimestamp")
      case "scala.math.BigDecimal" =>
        annotationJoin.filter(t => t._1.annotationCd == "bigDecimal")
      case "Long" =>
        annotationJoin.filter(t => t._1.annotationCd == "longNumber")
      case _ =>
        annotationJoin
    }).map(t => (t._1, t._2.getOrElse((None, None, None))))

    Future {
      Ok(views.html.maintenance.annotationEdit(annotationForm, annotationList, rowId))
    }
  }
}

