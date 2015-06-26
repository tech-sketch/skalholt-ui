package daos

import models.Tables._
import scala.concurrent.Future
import slick.driver.H2Driver.api._

object Screens extends AbstractScreens {

  /** ScreenEntity filter */
  def filterWithEntity(form: Product): Future[Seq[(ScreenRow, Option[String])]] = {
    val a = (for (
      screen <- filter(form, Screen);
      entity <- filter(form, ScreenEntity) if screen.screenId === entity.screenId
    ) yield (screen, entity.entityNmEn)).sortBy {
      _._1.screenId
    }
    db.run(a.result)
  }

  /** ID?? */
  def findByIdOpt(screenId: String) =
    db.run(Screen.filter(t => t.screenId === screenId).result.headOption)

  /** screen update */
  def updateScreen(e: ScreenRow) = {
    val a = Screen.filter(_.screenId === e.screenId)
      .map { s => (s.screenId, s.screenNm, s.jspNm, s.useCaseNm, s.actionClassId, s.subsystemNmJa, s.subsystemNmEn, s.screenType) }
      .update(e.screenId, e.screenNm, e.jspNm, e.useCaseNm, e.actionClassId, e.subsystemNmJa, e.subsystemNmEn, e.screenType)

    db.run(a)
  }

}
