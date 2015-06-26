package daos

import models.Tables._
import slick.driver.H2Driver.api._

object ScreenEntitys extends AbstractScreenEntitys {

  /** screenEntity update */
  def updateScreenEntity(e: ScreenEntityRow) = {
    val q = ScreenEntity.filter(_.screenId === e.screenId).update(e)
    db.run(q)
  }

  /** ScreenEntity?? */
  def filterByScreenId(screenId: String) = {
    val line: scala.math.BigDecimal = 1
    val q = ScreenEntity.filter(t => (t.screenId === screenId) && (t.lineNo === line)).result
    db.run(q.head)
  }

}
