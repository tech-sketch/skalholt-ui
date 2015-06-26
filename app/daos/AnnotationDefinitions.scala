package daos

import models.Tables._
import slick.driver.H2Driver.api._
import scala.concurrent.Future

object AnnotationDefinitions extends AbstractAnnotationDefinitions {

  /** Delete */
  def removeByDomainCd(domainCd: String) = {
    val q = AnnotationDefinition.filter(t => t.domainCd === domainCd).delete
    db.run(q)
  }

  /** domainCd only */
  def filterByDomainCd(domainCd: String) = {
    val q = AnnotationDefinition.filter(t => t.domainCd === domainCd).result
    db.run(q.headOption)
  }

  def joinAnnotationAndDefinition(domainCd: String): Future[Seq[(AnnotationRow, Option[(Option[String], Option[String], Option[String])])]] = {
    val q = for {
      (a, ad) <- Annotation joinLeft AnnotationDefinition on { case (a, ad) => a.annotationCd === ad.annotationCd && ad.domainCd === domainCd }

    } yield (a, ad.map(t => (t.definitionValue1, t.definitionValue2, t.definitionValue3)))

    db.run(q.result)
  }

}
