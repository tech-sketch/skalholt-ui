package controllers

import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class Application extends Controller {

  def index = Action.async {
    Future{Redirect(controllers.generate.routes.Generate.index)}
  }

}