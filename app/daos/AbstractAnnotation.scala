package models.db.generator

import models.Tables._
import daos.common.AbstractDao
import scala.slick.driver.H2Driver.simple._

abstract class AbstractAnnotations extends AbstractDao {
  /** 登録 */
  def insert(e: AnnotationRow)(implicit s: Session) = Annotation.insert(e)

  /** 更新 */
  def update(e: AnnotationRow)(implicit s: Session) =
    Annotation.filter(t => t.annotationCd === e.annotationCd).update(e)

  /** 削除 */
  def remove(annotationCd: String)(implicit s: Session) =
    Annotation.filter(t => t.annotationCd === annotationCd).delete

  /** ID検索 */
  def filterById(annotationCd: String)(implicit s: Session) =
    Annotation.filter(t => t.annotationCd === annotationCd).first

  /** 検索*/
  def all(implicit s: Session) =    Annotation.sortBy(_.annotationCd).list

  def length(implicit s: Session) =    Annotation.length.run
}