package controllers.generate

import controllers.common.CommonController
import forms.generate.{GenerateForm, GenerateForms}
import java.io.File
import skalholt.codegen.constants.GenConstants.GenParam
import skalholt.codegen.database.common.DBUtils
import skalholt.codegen.main.{Generate => Skalholt}
import play.api.Logger
import play.cache.Cache
import slick.ast.ColumnOption.PrimaryKey
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Generate extends CommonController with GenerateForm {

  val separator = System.getProperty("file.separator")

  def index = CommonAction { implicit request =>

    val currentDir = new File(".").getAbsoluteFile().getParent().replace(s"${separator}skalholt${separator}bin", "")
    val form = Cache.get("genparam") match {
      case p: GenParam =>
        GenerateForms(
          p.bizSlickDriver,
          p.bizJdbcDriver,
          p.bizUrl,
          p.bizUser,
          p.bizPassword,
          p.bizSchema,
          p.outputFolder.getOrElse(currentDir),
          p.pkg,
          p.ignoreTables)
      case _ =>
        GenerateForms(None, None, None, None, None, None, currentDir, None, None)
    }

    Future {
      Ok(views.html.generates.generate(generateForm.fill(form)))
    }
  }

  def generateAll = CommonAction { implicit request =>
    generateForm.bindFromRequest.fold(
      hasErrors = { form =>
        Future {
          Ok(views.html.generates.generate(form))
        }
      },
      success = { form =>
        val pkg = Some("models")
        val password = form.password match {
          case None => Some("")
          case x => x
        }
        val param = GenParam(form.slickDriver, form.jdbcDriver, form.url, form.user, password,
          form.schema, None, Some(form.outputFolder), pkg, None)
        Logger.debug("param=" + param)

        try {
          val model = DBUtils.getModel(form.jdbcDriver.get, form.url.get, form.schema.get, form.user, password)
          if (model.tables.length <= 0) {
            Future {
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("No tables.").bindFromRequest))
            }
          } else {
            val tableNms = model.tables.filter(t => {
              t.primaryKey.isEmpty && !t.columns.exists(_.options.contains(PrimaryKey))
            }).map(t => t.name.table)

            if (!tableNms.isEmpty)
              Future {
                BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError(s"Primary key does not exist in the tables[${tableNms.mkString(", ")}].").bindFromRequest))
              }
            else
              try {

                Cache.set("genparam", param)
                Skalholt.all(param)
                Future {
                  Redirect(controllers.generate.routes.Generate.index)
                    .flashing("success" -> "You have now generated the source code.")
                }
              } catch {
                case e: Exception =>
                  Future {
                    e.printStackTrace
                    BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Generator failure.").bindFromRequest))
                  }
              }
          }
        } catch {
          case e: Exception =>
            Future {
              e.printStackTrace
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Database connection error.").bindFromRequest))
            }
        }

      })
  }

  def importData = CommonAction { implicit request =>
    generateForm.bindFromRequest.fold(
      hasErrors = { form =>
        Future {
          Ok(views.html.generates.generate(form))
        }
      },
      success = { form =>
        val pkg = Some("models")
        val password = form.password match {
          case None => Some("")
          case x => x
        }
        val param = GenParam(form.slickDriver, form.jdbcDriver, form.url, form.user, password,
          form.schema, None, Some(form.outputFolder), pkg, None)

        try {
          val model = DBUtils.getModel(form.jdbcDriver.get, form.url.get, form.schema.get, form.user, form.password)
          if (model.tables.length <= 0) {
            Future {
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("No tables.").bindFromRequest))
            }
          } else {
            try {

              Cache.set("genparam", param)
              Skalholt.importData(param)
              Future {
                Redirect(controllers.generate.routes.Generate.index)
                  .flashing("success" -> "You have now imported database schema.")
              }
            } catch {
              case e: Exception =>
                Future {
                  e.printStackTrace
                  BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Generator failure.").bindFromRequest))
                }
            }
          }
        } catch {
          case e: Exception =>
            Future {
              e.printStackTrace
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Database connection error.").bindFromRequest))
            }
        }

      })
  }

  def generate = CommonAction { implicit request =>
    generateForm.bindFromRequest.fold(
      hasErrors = { form =>
        Future {
          Ok(views.html.generates.generate(form))
        }
      },
      success = { form =>
        val pkg = Some("models")
        val param = GenParam(form.slickDriver, form.jdbcDriver, form.url, form.user, form.password,
          form.schema, None, Some(form.outputFolder), pkg, None)

        try {
          val model = DBUtils.getModel(form.jdbcDriver.get, form.url.get, form.schema.get, form.user, form.password)
          if (model.tables.length <= 0) {
            Future {
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("No tables.").bindFromRequest))
            }
          } else {
            try {

              Cache.set("genparam", param)
              Skalholt.generate(param)
              Future {
                Redirect(controllers.generate.routes.Generate.index)
                  .flashing("success" -> "You have now imported database schema and generated the source code.")
              }
            } catch {
              case e: Exception =>
                Future {
                  e.printStackTrace
                  BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Generator failure.").bindFromRequest))
                }
            }
          }
        } catch {
          case e: Exception =>
            Future {
              e.printStackTrace
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Database connection error.").bindFromRequest))
            }
        }

      })
  }

  def regenerate = CommonAction { implicit request =>

    Cache.get("genparam") match {
      case p: GenParam =>
        val form = GenerateForms(
          p.bizSlickDriver,
          p.bizJdbcDriver,
          p.bizUrl,
          p.bizUser,
          p.bizPassword,
          p.bizSchema,
          p.outputFolder.get,
          p.pkg,
          p.ignoreTables)
        val param = GenParam(form.slickDriver, form.jdbcDriver, form.url, form.user, form.password,
          form.schema, None, Some(form.outputFolder), p.pkg, None)

        try {
          val model = DBUtils.getModel(form.jdbcDriver.get, form.url.get, form.schema.get, form.user, form.password)
          if (model.tables.length <= 0) {
            Future {
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("No tables.").bindFromRequest))
            }
          } else {
            try {
              Skalholt.generate(param)
              Future {
                Redirect(controllers.generate.routes.Generate.index)
                  .flashing("success" -> "You have now imported database schema and generated the source code.")
              }
            } catch {
              case e: Exception =>
                Future {
                  e.printStackTrace
                  BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Generator failure.").bindFromRequest))
                }
            }
          }
        } catch {
          case e: Exception =>
            Future {
              e.printStackTrace
              BadRequest(views.html.generates.generate(generateForm.fill(form).withGlobalError("Database connection error.").bindFromRequest))
            }
        }
      case _ =>
        Future {
          BadRequest(views.html.generates.generate(generateForm.withGlobalError("Code generate error.").bindFromRequest))
        }
    }

  }
}