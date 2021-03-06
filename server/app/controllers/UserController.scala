package controllers

import javax.inject._

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
import utils.Json._
import utils.SessionReader
import daos.UserDAO
import domain.LoginData
import domain.Role
import domain.User
import domain.ChangePasswordDTO
import org.mindrot.jbcrypt.BCrypt

@Singleton
class UserController @Inject()(cc: ControllerComponents, 
                               dao: UserDAO,
                               session: SessionReader)
                      extends AbstractController(cc) with I18nSupport {

  val logger = Logger(this.getClass())

  val dummyUser = User(None, "", "", None, None, false, false, Nil)

  def login = Action.async { implicit request =>
    logger.debug("Login method call")
    jsonBody(request) match {
      case Some(json) =>        
        val loginData = read[LoginData](json)        
        val logged: Future[Option[User]] = dao.validate(loginData.email, loginData.passwd)
        
        val p = Promise[Result]()
        logged onComplete {
          case Success(user)      =>
            logger.debug("Login success")
            user match {
              case None      => p success (Ok(write(user)))
              case Some(usr) => p success (Ok(write(user)).withSession(
                SessionReader.KeyUsername -> usr.email, 
                SessionReader.KeyUserId   -> usr.userId.getOrElse(0).toString,
                SessionReader.KeyRoles    -> usr.roles.map(_.value).mkString(" "),
                SessionReader.KeyCSRF     -> CSRF.getToken.map(_.value).getOrElse("")))
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
    logger.debug("CHANGE PASSWORD CALLED")
    jsonBody(request) match {
      case None => Future.successful(BadRequest)
      case Some(json) =>
      logger.debug("VALID JSON")
      session.withSessionUserId { userId =>
        logger.debug("VALID CALLER")
        val callerFuture = dao.find(userId)
        val dto: ChangePasswordDTO = read[ChangePasswordDTO](json)    
        
        val validCaller = callerFuture flatMap { optUser =>
          logger.debug(optUser.toString())
          optUser match {
            case None       => Future(None)
            case Some(user) => dao.validate(user.email, dto.oldPassword)
          }
        }

        val savedId = validCaller flatMap { optUser =>
          logger.debug(optUser.toString())          
          optUser match {
            case None       => Future(Some(0))
            case Some(user) => 
              val encrypted = BCrypt.hashpw(dto.newPassword, BCrypt.gensalt(12))
              logger.debug(user.toString())
              dao.save(user.copy(passwd = encrypted))
          }
        }

        val result = savedId map { optId =>
          optId match {
            case None => Ok(write(false))
            case Some(id) => 
              if(id == 0)
              Ok(write(false))
            else
              Ok(write(true))
          }
            
        }
        result
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
      session.withSessionUserId { callerId =>
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
      if(caller.canCreate(user))
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
    Future(Ok(write(true)).withSession(request.session - SessionReader.KeyUsername
                                                       - SessionReader.KeyUserId
                                                       - SessionReader.KeyRoles))
  }
}