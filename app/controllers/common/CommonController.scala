package controllers.common

import play.api._
import play.api.mvc._
import play.api.db.slick._

abstract class CommonController extends Controller {

  val success = "success"
  val error = "error"

  def CommonAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    LoggingAction {
      request =>
        f(request)
    }
  }

  def LoggingAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    DBAction.transaction { request =>
      f(request)
    }
  }
}