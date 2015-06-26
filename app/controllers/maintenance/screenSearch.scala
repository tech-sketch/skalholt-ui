package controllers.maintenance

import controllers.common.CommonController
import daos.Screens
import play.api.mvc._
import forms.maintenance.ScreenSearchForm
import play.cache.Cache
import skalholt.codegen.constants.GenConstants.GenParam
import skalholt.codegen.database.common.DBUtils
import skalholt.codegen.util.StringUtil._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class ScreenSearch extends CommonController with ScreenSearchForm {
  /** 初期 */
  def index = Action.async { implicit request =>
    screenSearchForm.bindFromRequest.fold(
      hasErrors = { form =>
        Future{BadRequest(views.html.maintenance.screenSearch(form, null, Seq.empty))}
      },
      success = { form =>

        val screens = Screens.filterWithEntity(form)

        val entityNmEns: Seq[(String, String)] = Cache.get("genparam") match {
          case p: GenParam =>
            try {
              DBUtils.getTables(p.bizJdbcDriver.get, p.bizUrl.get, p.bizSchema.get, p.bizUser, p.bizPassword)
                .map(t => (decapitalize(camelize(t.name.name)), decapitalize(camelize(t.name.name))))
            } catch {
              case e: Exception => Seq(("", "-- Tables is not found."))
            }
          case _ => Seq(("", "-- Tables is not found."))
        }

        screens.map( res => Ok(views.html.maintenance.screenSearch(screenSearchForm, res, entityNmEns)))
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
  def search = Action.async { implicit request =>
    screenSearchForm.bindFromRequest.fold(
      hasErrors = { form =>
        Future{BadRequest(views.html.maintenance.screenSearch(form, null,Seq.empty))}
      },
      success = { form =>

        val screens = Screens.filterWithEntity(form)
        screens.map(res => Ok(views.html.maintenance.screenSearch(screenSearchForm.bindFromRequest, res, Seq.empty)))
      })
  }
}

