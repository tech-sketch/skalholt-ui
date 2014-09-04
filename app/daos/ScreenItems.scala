package models.db.generator

import models.Tables._
import models.Tables.profile.simple._
import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import scala.language.postfixOps

object ScreenItems extends AbstractScreenItems {

  /** screenIdのみで削除 */
  def allDeleteScreenId(screenId: String)(implicit s: Session) =
    ScreenItem.filter(t => t.screenId === screenId).delete

  def findAnnotationDefinition(screenId: String)(implicit s: Session) = {
    val q = for {
      (i, a) <- ScreenItem leftJoin AnnotationDefinition on (_.domainCd === _.domainCd)
      if (i.screenId === screenId)
    } yield (i, a.annotationCd.?)
    q.list
  }

  def findItemAndAction(screenId: String)(implicit s: Session): List[(BigDecimal, models.Tables.ScreenActionRow)] = {
    val q =
      for {
        i <- ScreenItem if i.screenId === screenId
        a <- ScreenAction if (a.screenId === screenId && i.actionId === a.actionId)
      } yield (i.itemNo, a)
    q.list
  }
}