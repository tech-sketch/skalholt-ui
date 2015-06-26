package controllers.common

import play.api.mvc._
import scala.concurrent.Future

abstract class CommonController extends Controller {

  val success = "success"
  val error = "error"

  def CommonAction(f: Request[AnyContent] => Future[Result]): Action[AnyContent] = {
    LoggingAction {
      f
    }
  }

  def LoggingAction(f: Request[AnyContent] => Future[Result]): Action[AnyContent] = {
    Action.async {
      f
    }
  }
}