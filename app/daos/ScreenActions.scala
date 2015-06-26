package daos

import models.Tables._
import slick.driver.H2Driver.api._

object ScreenActions extends AbstractScreenActions {

  /** screenId?? */
  def allDeleteScreenIdActions(screenId: String) = {
    val q = ScreenAction.filter(t => t.screenId === screenId).delete
    db.run(q)
  }

}
