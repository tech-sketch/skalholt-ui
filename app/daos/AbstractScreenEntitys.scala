package models.db.generator

import models.Tables._
import daos.common.AbstractDao
import scala.slick.driver.H2Driver.simple._

abstract class AbstractScreenEntitys extends AbstractDao {
  /** 登録 */
  def insert(e: ScreenEntityRow)(implicit s: Session) = ScreenEntity.insert(e)

  /** 更新 */
  def update(e: ScreenEntityRow)(implicit s: Session) =
    ScreenEntity.filter(t => t.screenId === e.screenId && t.lineNo === e.lineNo).update(e)

  /** 削除 */
  def remove(screenId: String, lineNo: scala.math.BigDecimal)(implicit s: Session) =
    ScreenEntity.filter(t => t.screenId === screenId && t.lineNo === lineNo).delete

  /** ID検索 */
  def filterById(screenId: String, lineNo: scala.math.BigDecimal)(implicit s: Session) =
    ScreenEntity.filter(t => t.screenId === screenId && t.lineNo === lineNo).first
}