package daos

import models.Tables._
import daos.common.AbstractDao
import slick.driver.H2Driver.api._

abstract class AbstractScreenEntitys extends AbstractDao {

  /** 登録 */
  def insert(e: ScreenEntityRow) = {
    db.run(DBIO.seq(ScreenEntity += e))
  }

  /** 更新 */
  def update(e: ScreenEntityRow) =    {
      val q = ScreenEntity.filter(t => t.screenId === e.screenId && t.lineNo === e.lineNo).update(e)
      db.run(q)
    }

  /** 削除 */
  def remove(screenId: String, lineNo: scala.math.BigDecimal) =    {
      val q = ScreenEntity.filter(t => t.screenId === screenId && t.lineNo === lineNo).delete
      db.run(q)
    }

  /** ID検索 */
  def filterById(screenId: String, lineNo: scala.math.BigDecimal) =
    {
      val action = ScreenEntity.filter(t => t.screenId === screenId && t.lineNo === lineNo).result
      db.run(action.head)
    }
}