package controllers

import javax.inject._

import play.api.Logger
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import play.api.i18n.I18nSupport
import play.api.mvc.Results.InternalServerError
import daos.ChallengeDAO
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utils.Json._
import utils.SessionReader
import domain.Role
import domain.Challenge
import upickle.default._
import scala.util.Failure

@Singleton
class ChallengeController @Inject()(cc: ControllerComponents, 
                                    dao: ChallengeDAO, 
                                    session: SessionReader) 
                      extends AbstractController(cc) with I18nSupport {

  val logger = Logger(this.getClass())

  def challenges(pageSize: Int, page: Int) = Action.async { implicit request =>
    session.withRole(Role.Professor()) { () =>
      dao.find(pageSize, page).map(c => Ok(write(c)))
    }
  }
  
  /*
   * Only Professors can create Challenges
   */
  def create = Action.async { implicit request =>
    println(jsonBody(request))
    jsonBody(request) match {
      case None       => Future.successful(BadRequest)
      case Some(json) =>
        session.withRole(Role.Professor()) { () =>    
          dao.save(read[Challenge](json)).map { id: Option[Int] => 
            id match {
              case Some(id) =>
                Ok(write(id))
              case None     => Ok(write(0))
            }
          }.recoverWith { e => 
            logger.error(e.getMessage())
            Future(InternalServerError) 
          }
        }
      }
  }

  def delete(id: Int) = Action.async { implicit request =>
    session.withRole(Role.Professor()) { () =>
      dao.delete(id).map(deleted => if(deleted) Ok("") else InternalServerError(""))
    }
  }
}