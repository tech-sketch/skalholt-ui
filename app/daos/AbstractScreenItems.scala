package daos

import models.Tables._
import daos.common.AbstractDao
import slick.driver.H2Driver.api._
import scala.concurrent.Future

abstract class AbstractScreenItems extends AbstractDao {

  /** 登録 */
  def insert(e: ScreenItemRow) = {
    db.run(DBIO.seq(ScreenItem += e))
  }

  /** 更新 */
  def update(e: ScreenItemRow) =    {
      val q = ScreenItem.filter(t => t.screenId === e(0) && t.itemNo === e(1)).update(e)
      db.run(q)
    }

  /** 削除 */
  def remove(screenId: String, itemNo: scala.math.BigDecimal) =    {
      val q = ScreenItem.filter(t => t.screenId === screenId && t.itemNo === itemNo).delete
      db.run(q)
    }

  /** ID検索 */
  def filterById(screenId: String, itemNo: scala.math.BigDecimal) =    {
      val q =ScreenItem.filter(t => t.screenId === screenId && t.itemNo === itemNo).result
      db.run(q.head)
    }


    /** filter everything */
  def filter(form: Product):Future[Seq[ScreenItemRow]] =    {
      val q = filter(form, ScreenItem).result
      db.run(q)
    }
}