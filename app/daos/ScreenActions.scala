package models.db.generator

import models.Tables._
import models.Tables.profile.simple._
import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import scala.language.postfixOps

object ScreenActions extends AbstractScreenActions {

  /** screenIdのみで削除 */
  def allDeleteScreenIdActions(screenId: String)(implicit s: Session) =
    ScreenAction.filter(t => t.screenId === screenId).delete


}