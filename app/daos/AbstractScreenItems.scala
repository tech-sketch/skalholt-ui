package models.db.generator

import models.Tables._
import daos.common.AbstractDao
import scala.slick.driver.H2Driver.simple._

abstract class AbstractScreenItems extends AbstractDao {
  /** 登録 */
  def insert(e: ScreenItemRow)(implicit s: Session) = ScreenItem.insert(e)

  /** 更新 */
  def update(e: ScreenItemRow)(implicit s: Session) =
    ScreenItem.filter(t => t.screenId === e(0) && t.itemNo === e(1)).update(e)

  /** 削除 */
  def remove(screenId: String, itemNo: scala.math.BigDecimal)(implicit s: Session) =
    ScreenItem.filter(t => t.screenId === screenId && t.itemNo === itemNo).delete

  /** ID検索 */
  def filterById(screenId: String, itemNo: scala.math.BigDecimal)(implicit s: Session) =
    ScreenItem.filter(t => t.screenId === screenId && t.itemNo === itemNo).first


    /** filter everything */
  def filter(form: Product)(implicit s: Session):List[ScreenItemRow] =
    filter(form, ScreenItem).list
}