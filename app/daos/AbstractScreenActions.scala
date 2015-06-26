package daos

import models.Tables._
import daos.common.AbstractDao
import slick.driver.H2Driver.api._

abstract class AbstractScreenActions extends AbstractDao {

  /** 登録 */
  def insert(e: ScreenActionRow) = {
    db.run(DBIO.seq(ScreenAction += e))
  }

  /** 更新 */
  def update(e: ScreenActionRow) = {
    val q = ScreenAction.filter(t => t.screenId === e.screenId && t.actionId === e.actionId).update(e)
    db.run(q)
  }

  /** 削除 */
  def remove(screenId: String, actionId: String) = {
    val q = ScreenAction.filter(t => t.screenId === screenId && t.actionId === actionId).delete
    db.run(q)
  }

  /** ID検索 */
  def filterById(screenId: String, actionId: String) = {
    val q = ScreenAction.filter(t => t.screenId === screenId && t.actionId === actionId).result
    db.run(q.head)
  }
}