package util

import play.api.mvc.{AnyContent, Request}
import play.api.http.Writeable
import play.api.http.Status.OK
import play.api.mvc._

object Json {
  def jsonBody(request: Request[AnyContent]): Option[String] = request.body.asJson.map(_.toString)
}

object OkText {
  val OkTxt = new Results.Status(OK)
  def apply[C](content: C)(implicit writeable: Writeable[C]): Result =
    OkTxt(content).withHeaders("responseType" -> "text")
}
