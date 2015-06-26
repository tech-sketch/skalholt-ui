package controllers.maintenance

import controllers.common.CommonController
import play.api.libs.json._
import play.api.mvc._
import models.Tables._
import daos._
import forms.maintenance.{ScreenUpdateForm, ScreenUpdateForms}
import skalholt.codegen.constants.GenConstants.GenParam
import play.cache.Cache
import skalholt.codegen.database.common.DBUtils
import skalholt.codegen.util.StringUtil._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class ScreenUpdate extends CommonController with ScreenUpdateForm {
  /** 初期 */
  def index(screenId: String) = Action.async { implicit request =>
    val screenf = Screens.filterById(screenId)
    val screenEntityf = ScreenEntitys.filterByScreenId(screenId)
    val itemAndActionf = ScreenItems.findItemAndAction(screenId)

    val screenItemAndAnnotations = Await.result(ScreenItems.findAnnotationDefinition(screenId), Duration.Inf)
    val findDefinitionList: List[(ScreenItemRow, String)] =
      screenItemAndAnnotations.groupBy {
        case (screenItem, annotationCd) => screenItem
      }.map {
        case (key, value) => (key, value.map {
          case (i, annotationCd) => annotationCd.getOrElse("")
        }.mkString(","))
      }.toList.sortBy(_._1(1))

    val (form, screen) = (Await.result(screenf, Duration.Inf), Await.result(screenEntityf, Duration.Inf)) match {
      case (screen, screenEntity) => (ScreenUpdateForms(
        screen.screenId,
        screen.screenNm,
        screen.subsystemNmEn,
        screen.screenType,
        screenEntity.entityNmEn), screen)
    }

    val entityNmEns: Seq[(String, String)] = Cache.get("genparam") match {
      case p: GenParam =>
        try {
          DBUtils.getModel(p.bizJdbcDriver.get, p.bizUrl.get, p.bizSchema.get, p.bizUser, p.bizPassword)
            .tables.map(t => (decapitalize(camelize(t.name.table)), decapitalize(camelize(t.name.table))))
        } catch {
          case e: Exception => Seq(("", "-- Tables is not found."))
        }
      case _ => Seq(("", "-- Tables is not found."))
    }
    itemAndActionf.map { case itemAndAction =>
      Ok(views.html.maintenance.screenUpdate(screenUpdateForm.fill(form), itemAndAction, findDefinitionList, entityNmEns, screen.screenNm))
    }
  }

  /** 更新 */
  private def j2sBI(jsValue: JsValue, target: String) = BigDecimal((jsValue \ (target)).as[String])

  private def j2s(jsValue: JsValue, target: String) = (jsValue \ (target)).as[String]

  private def j2os(jsValue: JsValue, target: String) = (jsValue \ (target)).asOpt[String]

  private def updateScreens(screen: JsValue) =
    Screens.updateScreen(ScreenRow(
      j2s(screen, "screenId"),
      j2os(screen, "screenNm"),
      j2os(screen, "screenNm"),
      j2os(screen, "screenNm"),
      j2os(screen, "screenId"),
      None,
      j2os(screen, "subsystemNmEn"),
      j2os(screen, "subsystemNmEn"),
      None,
      j2os(screen, "screenType"),
      None,
      None))

  private def updateScreenEntitys(screen: JsValue) {
    ScreenEntitys.updateScreenEntity(
      ScreenEntityRow(
        j2s(screen, "screenId"),
        1,
        j2os(screen, "entityNmEn"),
        j2os(screen, "entityNmEn")))
  }

  private def insertScreenItems(item: JsValue) = {
    val domain = j2s(item, "screenId") + "-" + j2sBI(item, "itemNo")
    ScreenItems.insert(
      ScreenItemRow(
        j2s(item, "screenId"),
        j2sBI(item, "itemNo"),
        j2os(item, "itemNmEn"),
        j2os(item, "insideItemNmEn"),
        j2os(item, "itemNmEn"),
        None,
        j2os(item, "insideItemNmEn"),
        None,
        j2os(item, "component"),
        j2os(item, "activeKb"),
        None,
        Some(domain),
        Some(domain),
        None,
        None,
        j2os(item, "requiredKb"),
        None,
        None,
        None,
        None,
        None,
        None,
        j2os(item, "searchConditionKb"),
        None,
        j2os(item, "actionId"),
        j2os(item, "searchresultFlag"),
        None))
  }

  private def insertScreenActions(action: JsValue) =
    ScreenActions.insert(
      ScreenActionRow(
        j2s(action, "screenId"),
        j2s(action, "actionId"),
        j2os(action, "actionNmEn"),
        j2os(action, "actionNmEn"),
        j2os(action, "forwardScreenId"),
        j2os(action, "errorScreenId"),
        j2os(action, "actionNmEn"),
        None))

  def update = Action.async { implicit request =>

    def createScreenItem(screenId: String)(implicit json: JsValue) = {
      /** All delete */
      ScreenItems.allDeleteScreenId(screenId)

      /** screenItem */
      (json \ "jsonItem").asOpt[JsArray].map { jsArray =>
        if (jsArray.value.exists(item => j2s(item, "itemNo").isEmpty)) BadRequest("")
        else {
          jsArray.value.foreach { item =>
            insertScreenItems(item)
            if (createScreenAction(screenId) == success) {
              /** annotation definition */
              if (j2os(item, "annotationCd") != None) {
                val domainCd = (j2s(item, "screenId")) + "-" + (j2sBI(item, "itemNo"))
                AnnotationDefinitions.removeByDomainCd(domainCd)
                val annotations: Array[String] = ((j2s(item, "annotationCd")).split(","))
                annotations.foreach { annotation =>
                  AnnotationDefinitions.insert(AnnotationDefinitionRow(domainCd, annotation, None, None, None, None, None, None, None))
                }
              }
            } else {
              error
            }
          }
          success
        }
      }.getOrElse {
        error
      }
    }

    def createScreenAction(screenId: String)(implicit json: JsValue) = {
      ScreenActions.allDeleteScreenIdActions(screenId)

      /** screenAction */
      (json \ "jsonAction").asOpt[JsArray].map { jsArray =>
        jsArray.value.foreach { action =>
          if (!j2os(action, "actionId").isEmpty) {
            /** All delete */
            insertScreenActions(action)
          }
        }
        success
      }.getOrElse {
        error
      }
    }

    request.body.asJson.map { implicit json =>

      /** screen */
      (json \ "jsonScreen").asOpt[JsArray].map { jsArray =>
        val screen = jsArray.value.head
        val screenId = j2s(screen, "screenId")
        Await.result(Screens.findByIdOpt(screenId), Duration.Inf) match {
          case Some(_) =>
            updateScreens(screen)
            updateScreenEntitys(screen)
            if (createScreenItem(screenId) == success) {
              Future {
                Ok("")
              }
            } else {
              Future {
                BadRequest("")
              }
            }
          case _ =>
            Future {
              BadRequest("")
            }
        }

      }.getOrElse {
        Future {
          BadRequest("")
        }
      }
    }.getOrElse {
      Future {
        BadRequest("")
      }
    }
  }

  /** all delete */
  def allDelete = Action.async { implicit request =>

    def deleteAnnotationDefinition(screenId: String)(implicit json: JsValue) = {
      (json \ "jsonItem").asOpt[JsArray].map { jsArray =>
        jsArray.value.foreach { item =>

          /** annotation definition */
          val domainCd = screenId + "-" + (j2sBI(item, "itemNo"))
          AnnotationDefinitions.removeByDomainCd(domainCd)
        }
        success
      }.getOrElse {
        error
      }

    }

    request.body.asJson.map { implicit json =>
      (json \ "jsonScreen").asOpt[JsArray].map { jsArray =>
        val screen = jsArray.value.head
        val screenId = j2s(screen, "screenId")

        if (!screenId.isEmpty) {
          Screens.remove(screenId)
          ScreenEntitys.remove(screenId, 1)
          ScreenItems.allDeleteScreenId(screenId)
          ScreenActions.allDeleteScreenIdActions(screenId)
          deleteAnnotationDefinition(screenId)
          Future {
            Ok("")
          }
        } else {
          Future {
            BadRequest("")
          }
        }
      }.getOrElse {
        Future {
          BadRequest("")
        }
      }

    }.getOrElse {
      Future {
        BadRequest("")
      }
    }

  }

}




