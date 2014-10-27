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
import skalholt.codegen.database.{ Screens, ScreenItems, ScreenEntitys, ScreenActions }
import play.cache.Cache
import skalholt.codegen.constants.GenConstants.GenParam
import skalholt.codegen.database.common.DBUtils
import skalholt.codegen.util.StringUtil._

object AnnotationEdit extends CommonController with AnnotationEditForm {
  /** Select */
  def select(rowId: String, screenId: String, entityNm: String, itemNo: String, insideItemNmEn: String) = DBAction.transaction { implicit request =>

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
    val annotationJoin = AnnotationDefinitions.joinAnnotationAndDefinition(domainCd)
    val annotationList = annotationType match {
      case "Int" => {
        annotationJoin.filter(t => t._1.annotationCd == "number")
      }
      case "String" => {
        annotationJoin.filter(t => t._1.annotationCd == "text")

      }
      case "java.sql.Date" => {
        annotationJoin.filter(t => t._1.annotationCd == "sqlDate") ::: annotationJoin.filter(t => t._1.annotationCd == "sqlTimestamp")

      }
      case "scala.math.BigDecimal" => {
        annotationJoin.filter(t => t._1.annotationCd == "bigDecimal")

      }
      case "Long" => {
        annotationJoin.filter(t => t._1.annotationCd == "longNumber")
      }
      case _ => {
        annotationJoin
      }

    }

    Ok(views.html.maintenance.annotationEdit(annotationForm, annotationList, rowId))

  }

}

