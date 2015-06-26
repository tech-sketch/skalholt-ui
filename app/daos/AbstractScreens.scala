package daos

import models.Tables._
import daos.common.AbstractDao
import scala.concurrent.Future
import slick.driver.H2Driver.api._

abstract class AbstractScreens extends AbstractDao {

  /** 登録 */
  def insert(e: ScreenRow) = {
    db.run(DBIO.seq(Screen += e))
  }

  /** 更新 */
  def update(e: ScreenRow) ={
    val q = Screen.filter(t => t.screenId === e.screenId).update(e)
    db.run(q)
  }

  /** 削除 */
  def remove(screenId: String) = {
    val q = Screen.filter(t => t.screenId === screenId).delete
    db.run(q)
  }

  /** ID検索 */
  def filterById(screenId: String) = {
    val action = Screen.filter(t => t.screenId === screenId).result
    db.run(action.head)
  }

  /** filter everything */
  def filter(form: Product): Future[Seq[ScreenRow]] = {
    val action = filter(form, Screen).result
    db.run(action)
  }
}