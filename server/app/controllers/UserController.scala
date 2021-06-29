package controllers

import javax.inject._

//import jsmessages.JsMessagesFactory
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.filters.csrf.CSRF
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.Success
import scala.util.Failure
import upickle.default._

import shared.SharedMessages
import util.Json._
import daos.UserDAO
import domain.LoginData
import domain.Role
import domain.User
import domain.UserDTO
import domain.ChangePasswordDTO
import org.mindrot.jbcrypt.BCrypt

@Singleton
class UserController @Inject()(cc: ControllerComponents, dao: UserDAO)//, JsMessagesFactory: JsMessagesFactory) 
                      extends AbstractController(cc) with I18nSupport {

  val logger = Logger(this.getClass())

  val dummyUser = User(None, "", "", None, None, false, false, Nil)

  def withSessionUsername(f: String => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get("username").map(f).getOrElse(Future.successful(Ok(write(Seq.empty[String]))))
  }

  def withSessionUserId(f: Int => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get("userId").map(_.toInt).map(f).getOrElse(Future.successful(Ok(write(Seq.empty[String]))))
  }

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
            user match {
              case None      => p success (Ok(write(user)))
              case Some(usr) => p success (Ok(write(user)).withSession(
                           "username"  -> usr.username, 
                           "userid"    -> usr.userId.getOrElse(0).toString,
                           "csrfToken" -> CSRF.getToken.map(_.value).getOrElse("")))
            }
          case Failure(exception) => 
            logger.debug("Login failure")
            logger.error(exception.getMessage())
            val result = Ok(write(None))
            println(result.header)
            p success (result)
        }
        p.future
      case None => Future.successful(BadRequest)
    }
  }

  /*
   * Change the password of the signed User.
   */
  def changePassword = Action.async { implicit request =>
    logger.warn("CHANGE PASSWORD CALLED")
    jsonBody(request) match {
      case None => Future.successful(BadRequest)
      case Some(json) =>
      logger.warn("VALID JSON")
      withSessionUserId { userId =>
        logger.warn("VALID CALLER")
        val callerFuture = dao.find(userId)
        val dto: ChangePasswordDTO = read[ChangePasswordDTO](json)    
        
        val validCaller = callerFuture flatMap { optUser =>
          optUser match {
            case None       => Future(None)
            case Some(user) => dao.validate(user.email, dto.oldPassword)
          }
        }

        val result = dao.find(userId).andThen {
          case Success(Some(user)) => dao.validate(user.email, dto.oldPassword)
        }.recoverWith {
          case Success(Some(user)) => dao.save(user.copy(passwd = dto.newPassword))
        }.recover {
          case Success(Some(id)) => 
            if(id == 0)
              Ok(write(false))
            else
              Ok(write(true))
        }
        val validCallerFuture = for {
          callerOpt <- callerFuture
          valid     <- dao.validate(callerOpt.getOrElse(dummyUser).email, dto.oldPassword) 
        } yield valid

        val savedIdFuture = validCallerFuture.map { validCaller =>
          validCaller match {
            case None => Future(Some(0))
            case Some(user) => dao.save(user.copy(passwd = dto.newPassword))
          }
        }

        val res = for {
          idOpt <- savedIdFuture
          id = idOpt.getOrElse(0)
          result = 
            if(id == 0)
              Ok(write(false))
            else
              Ok(write(true))
        } yield result 
        
        res
      }
    }        
  }

  /*
   * Creates a new User.
   * @returns The id of the created User or 0 if failed.
   */
  def create = Action.async { implicit request =>
    jsonBody(request) match {
      case None => Future.successful(BadRequest)
      case Some(json) =>
      withSessionUserId { callerId =>
        val user = read[User](json)
        createIfValid(user, callerId)
      }
    }
  }

  /*
   * Validates if the user to create has valid roles.
   * Admin can create users with roles Professor and Student.
   * Professors can create users with role Student.
   * Nobody can create users with Admin or Guest roles.
   */
  def createIfValid(user: User, callerId: Int): Future[Result] = {
    val callerUser = dao.find(callerId)

    val saved = for {
      callerOpt <- callerUser
      caller = callerOpt.get
      if(!user.roles.contains(Role.Admin)      && !user.roles.contains(Role.Guest)       &&
          (user.roles.contains(Role.Professor) &&  caller.roles.contains(Role.Admin))    ||
          (user.roles.contains(Role.Student)   && (caller.roles.contains(Role.Professor) ||
                                                   caller.roles.contains(Role.Admin))))
        savedId <- dao.save(user)
    } yield savedId
    
    saved.map{ optId => 
      optId match {
        case None     => Ok(write(0))
        case Some(id) => Ok(write(id))
      }
    }
  }

  def logout = Action.async { request =>
    Future(Ok(write(true)).withSession(request.session - "username"))
  } 
  
}
