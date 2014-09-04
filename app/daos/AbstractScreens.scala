package models.db.generator

import models.Tables._
import daos.common.AbstractDao
import scala.slick.driver.H2Driver.simple._

abstract class AbstractScreens extends AbstractDao {
  /** 登録 */
  def insert(e: ScreenRow)(implicit s: Session) = Screen.insert(e)

  /** 更新 */
  def update(e: ScreenRow)(implicit s: Session) =
    Screen.filter(t => t.screenId === e.screenId).update(e)

  /** 削除 */
  def remove(screenId: String)(implicit s: Session) =
    Screen.filter(t => t.screenId === screenId).delete

  /** ID検索 */
  def filterById(screenId: String)(implicit s: Session) =
    Screen.filter(t => t.screenId === screenId).first

  /** filter everything */
  def filter(form: Product)(implicit s: Session): List[ScreenRow] =
    filter(form, Screen).list
}