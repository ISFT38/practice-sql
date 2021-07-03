package utils

import play.api.mvc.{AnyContent, Request}
import play.api.mvc._
import scala.concurrent.Future
import play.api.mvc._
import upickle.default._

object Json {
  def jsonBody(request: Request[AnyContent]): Option[String] = 
    request.body.asJson.map(_.toString)

}

