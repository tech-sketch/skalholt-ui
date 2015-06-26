package daos

import models.Tables._
import daos.common.AbstractDao
import slick.driver.H2Driver.api._
import scala.concurrent.Future

abstract class AbstractAnnotationDefinitions extends AbstractDao {

  /** insert */
  def insert(e: AnnotationDefinitionRow) = {
    db.run(DBIO.seq(AnnotationDefinition += e))
  }

  /** update */
  def update(e: AnnotationDefinitionRow) = {
    val q = AnnotationDefinition.filter(t => t.domainCd === e.domainCd && t.annotationCd === e.annotationCd).update(e)
    db.run(q)
  }

  /** delete */
  def remove(domainCd: String, annotationCd: String) = {
    val q = AnnotationDefinition.filter(t => t.domainCd === domainCd && t.annotationCd === annotationCd).delete
    db.run(q)
  }

  /** find by id */
  def filterById(domainCd: String, annotationCd: String) = {
    val q = AnnotationDefinition.filter(t => t.domainCd === domainCd && t.annotationCd === annotationCd).result
    db.run(q.head)
  }

  /** find all data */
  def all = {
    val q = AnnotationDefinition.sortBy(f => (f.domainCd, f.annotationCd)).result
    db.run(q)
  }

  /** filter everything */
  def filter(form: Product): Future[Seq[AnnotationDefinitionRow]] = {
    val q = filter(form, AnnotationDefinition).result
    db.run(q)
  }

}