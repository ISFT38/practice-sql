package controllers

import javax.inject._

import shared.SharedMessages
import play.api.mvc._
import upickle.default._
import play.api.i18n.Langs
import play.api.i18n.Messages
import play.api.i18n.MessagesImpl

@Singleton
class Application @Inject()(cc: ControllerComponents, langs: Langs) extends AbstractController(cc) {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

}
