package models.db.generator

import models.Tables._
import models.Tables.profile.simple._

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import scala.language.postfixOps

object AnnotationDefinitions extends AbstractAnnotationDefinitions {

  /** Delete */
  def removeByDomainCd(domainCd :String) (implicit s: Session) =
    AnnotationDefinition.filter(t => t.domainCd === domainCd).delete

  /** domainCd only */
  def filterByDomainCd(domainCd :String)(implicit s: Session) =
    AnnotationDefinition.filter(t => t.domainCd === domainCd).firstOption

   def joinAnnotationAndDefinition(domainCd:String)(implicit s:Session): List[(AnnotationRow, (Option[String], Option[String], Option[String]))] = {
    val q = for{
      (a,ad) <- Annotation leftJoin AnnotationDefinition on { case (a, ad) => a.annotationCd === ad.annotationCd && ad.domainCd === domainCd}

    }yield(a,(ad.definitionValue1, ad.definitionValue2, ad.definitionValue3))

    q.list
  }

}
