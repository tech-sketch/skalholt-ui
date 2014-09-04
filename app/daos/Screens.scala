package models.db.generator

import models.Tables._
import models.Tables.profile.simple._
import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import scala.language.postfixOps

object Screens extends AbstractScreens {

  /** ScreenEntity filter */
  def filterWithEntity(form: Product)(implicit s: Session): List[(ScreenRow, Option[String])] = {
    (for (
      screen <- filter(form, Screen);
      entity <- filter(form, ScreenEntity) if screen.screenId === entity.screenId
    ) yield (screen, entity.entityNmEn)).sortBy { _._1.screenId }.list
  }

  /** ID検索 */
  def findByIdOpt(screenId: String)(implicit s: Session) =
    Screen.filter(t => t.screenId === screenId).firstOption

  /** screen update*/
  def updateScreen(e: ScreenRow)(implicit s: Session) ={
    Screen.filter(_.screenId === e.screenId).map(_.screenId).update(e.screenId)
    Screen.filter(_.screenId === e.screenId).map(_.screenNm).update(e.screenNm)
    Screen.filter(_.screenId === e.screenId).map(_.jspNm).update(e.jspNm)
    Screen.filter(_.screenId === e.screenId).map(_.useCaseNm).update(e.useCaseNm)
    Screen.filter(_.screenId === e.screenId).map(_.actionClassId).update(e.actionClassId)
    Screen.filter(_.screenId === e.screenId).map(_.subsystemNmJa).update(e.subsystemNmJa)
    Screen.filter(_.screenId === e.screenId).map(_.subsystemNmEn).update(e.subsystemNmEn)
    Screen.filter(_.screenId === e.screenId).map(_.screenType).update(e.screenType)
  }

}