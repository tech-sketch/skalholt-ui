package models.db.generator

import scala.slick.driver.JdbcDriver
import scala.slick.driver.JdbcDriver.simple ._
import scala.slick.jdbc.meta.{ MTable, createModel }
import daos.common.AbstractDao

object Generator extends AbstractDao {
  def model(schema: Option[String])(implicit s: Session) = {
    val tables = MTable.getTables(None, schema, None, None).list
    createModel(tables, JdbcDriver)
  }
}