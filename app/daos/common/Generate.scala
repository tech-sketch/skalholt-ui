package daos.common

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.JdbcDriver
import scala.slick.driver.JdbcDriver.simple ._
import scala.slick.jdbc.meta.{ MTable, createModel }

object Generate extends AbstractDao {
  def model(schema: Option[String])(implicit s: Session) = {
    val tables = MTable.getTables(None, schema, None, None).list
    createModel(tables, JdbcDriver)
  }
}