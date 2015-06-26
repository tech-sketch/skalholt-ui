package daos

import models.Tables._
import slick.driver.H2Driver.api._
import scala.concurrent.Future

object ScreenItems extends AbstractScreenItems {

  /** screenId?? */
  def allDeleteScreenId(screenId: String) =    {
    val q = ScreenItem.filter(t => t.screenId === screenId).delete
    db.run(q)
  }

  def findAnnotationDefinition(screenId: String):Future[Seq[(ScreenItemRow,Option[String])]] = {
    val q = for {
      (i, a) <- ScreenItem joinLeft AnnotationDefinition on (_.domainCd === _.domainCd)
      if (i.screenId === screenId)
    } yield (i, a.map(_.annotationCd))
    db.run(q.result)
  }

  def findItemAndAction(screenId: String): Future[Seq[(BigDecimal, models.Tables.ScreenActionRow)]] = {
    val q =
      for {
        i <- ScreenItem if i.screenId === screenId
        a <- ScreenAction if (a.screenId === screenId && i.actionId === a.actionId)
      } yield (i.itemNo, a)
    db.run(q.result)
  }
}
