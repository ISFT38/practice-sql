package controllers

import javax.inject._

//import jsmessages.JsMessagesFactory
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default._

import shared.SharedMessages
import daos.UserDAO
import domain.LoginData
import scala.concurrent.Promise
import scala.util.Success
import scala.util.Failure
import domain.UserDTO
import util.Json._
import util.OkText

@Singleton
class UserController @Inject()(cc: ControllerComponents, dao: UserDAO)//, JsMessagesFactory: JsMessagesFactory) 
                      extends AbstractController(cc) with I18nSupport {

  val logger = Logger(this.getClass())

  def login = Action.async { implicit request =>
    logger.debug("login method call")
    jsonBody(request) match {
      case Some(json) =>        
        val loginData = read[LoginData](json)        
        val logged: Future[Option[UserDTO]] = dao.validate(loginData.email, loginData.passwd)
        
        val p = Promise[Result]()
        logged onComplete {
          case Success(user)      => 
            logger.debug("Login success")
            p success (OkText(write(user)))
          case Failure(exception) => 
            logger.debug("Login failure")
            logger.error(exception.getMessage())
            val result = OkText(write(None))
            println(result.header)
            p success (result)
        }
        p.future
      case None => Future.successful(BadRequest)
    }
  }
}
