package daos

import models.Tables._
import daos.common.AbstractDao
import slick.driver.H2Driver.api._

abstract class AbstractAnnotations extends AbstractDao {

  /** 登録 */
  def insert(e: AnnotationRow) = {
    db.run(DBIO.seq(Annotation += e))
  }

  /** 更新 */
  def update(e: AnnotationRow) = {
    val q = Annotation.filter(t => t.annotationCd === e.annotationCd).update(e)
    db.run(q)
  }

  /** 削除 */
  def remove(annotationCd: String) = {
    val q = Annotation.filter(t => t.annotationCd === annotationCd).delete
    db.run(q)
  }

  /** ID検索 */
  def filterById(annotationCd: String) = {
    val action = Annotation.filter(t => t.annotationCd === annotationCd).result
    db.run(action.head)
  }

  /** 検索 */
  def all = {
    val q = Annotation.sortBy(_.annotationCd).result
    db.run(q)
  }

  def length = {
    val q = Annotation.length
    db.run(q.result)
  }
}