package models.db.generator

import models.Tables._
import daos.common.AbstractDao
import scala.slick.driver.H2Driver.simple._

abstract class AbstractAnnotationDefinitions extends AbstractDao {
  /** insert */
  def insert(e: AnnotationDefinitionRow)(implicit s: Session) =   AnnotationDefinition.insert(e)

  /** update */
  def update(e: AnnotationDefinitionRow)(implicit s: Session) =
    AnnotationDefinition.filter(t => t.domainCd === e.domainCd && t.annotationCd === e.annotationCd).update(e)

  /** delete */
  def remove(domainCd :String, annotationCd :String) (implicit s: Session) =
    AnnotationDefinition.filter(t => t.domainCd === domainCd && t.annotationCd === annotationCd).delete

  /** find by id */
  def filterById(domainCd :String, annotationCd :String)(implicit s: Session) =
    AnnotationDefinition.filter(t => t.domainCd === domainCd && t.annotationCd === annotationCd).first

  /** find all data */
  def all(implicit s: Session) =
    AnnotationDefinition.sortBy(f => (f.domainCd, f.annotationCd)).list

  /** filter everything */
  def filter(form: Product)(implicit s: Session):List[AnnotationDefinitionRow] =
    filter(form, AnnotationDefinition).list

}