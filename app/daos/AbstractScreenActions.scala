package models.db.generator

import models.Tables._
import daos.common.AbstractDao
import scala.slick.driver.H2Driver.simple._

abstract class AbstractScreenActions extends AbstractDao {
  /** 登録 */
  def insert(e: ScreenActionRow)(implicit s: Session) = ScreenAction.insert(e)

  /** 更新 */
  def update(e: ScreenActionRow)(implicit s: Session) =
    ScreenAction.filter(t => t.screenId === e.screenId && t.actionId === e.actionId).update(e)

  /** 削除 */
  def remove(screenId: String, actionId: String)(implicit s: Session) =
    ScreenAction.filter(t => t.screenId === screenId && t.actionId === actionId).delete

  /** ID検索 */
  def filterById(screenId: String, actionId: String)(implicit s: Session) =
    ScreenAction.filter(t => t.screenId === screenId && t.actionId === actionId).first
}