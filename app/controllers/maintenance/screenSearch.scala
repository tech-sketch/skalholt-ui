package controllers.maintenance

import controllers.common.CommonController
import play.api.mvc._
import models.db.generator.Screens
import models.Tables._
import forms.maintenance.{ ScreenSearchForm, ScreenSearchForms }
import play.api.libs.json._
import play.api.db.slick._
import play.cache.Cache
import skalholt.codegen.constants.GenConstants.GenParam
import skalholt.codegen.database.common.DBUtils
import skalholt.codegen.util.StringUtil._

object ScreenSearch extends CommonController with ScreenSearchForm {
  /** 初期 */
  def index = DBAction.transaction { implicit request =>
    screenSearchForm.bindFromRequest.fold(
      hasErrors = { form =>
        BadRequest(views.html.maintenance.screenSearch(form, null, Seq.empty))
      },
      success = { form =>

        val screens = Screens.filterWithEntity(form)

        val entityNmEns: Seq[(String, String)] = Cache.get("genparam") match {
          case p: GenParam =>
            try {
              DBUtils.getModel(p.bizJdbcDriver.get, p.bizUrl.get, p.bizSchema.get, p.bizUser, p.bizPassword).
                tables.map(t => (decapitalize(camelize(t.name.table)), decapitalize(camelize(t.name.table))))
            } catch {
              case e: Exception => Seq(("", "-- Tables is not found."))
            }
          case _ => Seq(("", "-- Tables is not found."))
        }

        Ok(views.html.maintenance.screenSearch(screenSearchForm, screens, entityNmEns))
      })

  }

  private def searchForm(form: forms.maintenance.ScreenSearchForms, screenType: Option[String]) =
    forms.maintenance.ScreenSearchForms(
      form.screenId,
      form.screenNm,
      form.subsystemNmEn,
      screenType,
      form.entityNmEn)

  /** 検索 */
  def search = DBAction.transaction { implicit request =>
    screenSearchForm.bindFromRequest.fold(
      hasErrors = { form =>
        BadRequest(views.html.maintenance.screenSearch(form, null,Seq.empty))
      },
      success = { form =>

        val screens = Screens.filterWithEntity(form)
        Ok(views.html.maintenance.screenSearch(screenSearchForm.bindFromRequest, screens, Seq.empty))
      })

  }

}

