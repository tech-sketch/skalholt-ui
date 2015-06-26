import play.api._
import org.h2.tools.Server
import play.cache.Cache
import skalholt.codegen.constants.GenConstants.GenParam
import java.io.File

object Global extends GlobalSettings {
  val separator = System.getProperty("file.separator")
  override def onStart(app: Application) {
    Logger.info("Application has started")
    val currentDir = new File(".").getAbsoluteFile().getParent().replace(s"${separator}skalholt${separator}bin", "")
    val param = GenParam(
      Some("slick.driver.H2Driver"),
      Some("org.h2.Driver"),
      Some("jdbc:h2:tcp://localhost:9092/./skalholt"),
      Some("sa"),
      Some(""),
      Some("SAMPLE"),
      None,
      Some(currentDir),
      Some("models"),
      Some(List.empty))
    Cache.set("genparam", param)

  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    Cache.remove("genparam")
  }

  //override def onError(request: RequestHeader, ex: Throwable) = {
  //    Future.successful(InternalServerError(
  //      views.html.errorPage(ex)
  //    ))
  //  }
  //
  // override def onHandlerNotFound(request: RequestHeader) = {
  //    Future.successful(NotFound(
  //      views.html.notFoundPage(request.path)
  //    ))
  // }
  //
  //  override def onBadRequest(request: RequestHeader, error: String) = {
  //    Future.successful(BadRequest("Bad Request: " + error))
  //  }

}