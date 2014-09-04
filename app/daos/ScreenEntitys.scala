package models.db.generator

import models.Tables._
import models.Tables.profile.simple._
import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import scala.language.postfixOps

object ScreenEntitys extends AbstractScreenEntitys {
  /** screenEntity update*/
  def updateScreenEntity(e: ScreenEntityRow)(implicit s: Session) = {
    ScreenEntity.filter(_.screenId === e.screenId).map(_.screenId).update(e.screenId)
    ScreenEntity.filter(_.screenId === e.screenId).map(_.lineNo).update(e.lineNo)
    ScreenEntity.filter(_.screenId === e.screenId).map(_.entityNmJa).update(e.entityNmJa)
    ScreenEntity.filter(_.screenId === e.screenId).map(_.entityNmEn).update(e.entityNmEn)

  }

  /** ScreenEntity検索 */
  def filterByScreenId(screenId: String)(implicit s: Session) = {
    val line: scala.math.BigDecimal = 1
    ScreenEntity.filter(t => (t.screenId === screenId) && (t.lineNo === line)).first
  }

}