package utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.Results.BadRequest
import play.api.mvc.Results.Forbidden
import play.api.mvc._
import javax.inject._
import domain.User
import daos.UserDAO
import scala.util.Success
import scala.util.Failure
import scala.util.Try
import domain.Role

@Singleton
class SessionReader @Inject()(dao: UserDAO) {

  import SessionReader._

  def withSessionUsername(f: String => Future[Result])
                         (implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get(KeyUsername).map(f).getOrElse(Future.successful(BadRequest))
  }

  def withSessionUserId(f: Int => Future[Result])
                       (implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get(KeyUserId).map(_.toInt).map(f).getOrElse(Future.successful(BadRequest))
  }

  def safeInt(string: String): Int = Try[Int] { string.toInt }.getOrElse(0)

  def withSessionUser(f: User => Future[Result])
                      (implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get(KeyUserId).map { idString =>
      dao.find(safeInt(idString)).map(_.getOrElse(User.guest)).flatMap(f)
    }.getOrElse(Future.successful(BadRequest))
  }

  def withRole(role: Role)(f: () => Future[Result])
                      (implicit request: Request[AnyContent]): Future[Result] =
    request.session.get(KeyRoles) match {
      case None    => Future.successful(Forbidden)
      case Some(s) =>
        val roles = s.split(" ").map(Role.fromValue(_))
        if(roles.contains(role)) 
          f()
        else 
          Future.successful(Forbidden)
  }
}

object SessionReader {
  val KeyUserId   = "userId"
  val KeyUsername = "username"
  val KeyRoles    = "roles"
  val KeyCSRF     = "csrfToken"
}