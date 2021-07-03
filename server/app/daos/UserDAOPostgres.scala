package daos

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.Success
import org.mindrot.jbcrypt.BCrypt
import io.getquill._
import play.api.Logger

import domain._
import scala.util.Failure

class UserDAOPostgres extends UserDAO {
  
  val logger: Logger = Logger(this.getClass())
  
  import db.ctx
  import db.ctx._

  implicit val encodeRole = MappedEncoding[Role, String](_.value)
  implicit val decodeRole = MappedEncoding[String, Role]{ string =>
    Role.fromValue(string)
  }

  implicit val userInsertMeta = insertMeta[User](_.userId)
  
  val userTable = quote { querySchema[User]("user_data") }

  override def save(user: User): Future[Option[Int]] = user.userId match {
    case None => 
      logger.debug("SAVE - INSERT")
      if(user.email == "" || user.passwd == "") Future(Some(0))
      else {
        val q = quote {
          userTable.insert(lift(user.copy(passwd = 
                                BCrypt.hashpw(user.passwd, BCrypt.gensalt(12)))))
                                .returningGenerated(_.userId)
        }
        ctx.run(q)
      }
    case Some(value) => 
      logger.debug("SAVE - UPDATE")
      val q = quote {
        userTable.filter(_.userId == lift(user.userId)).update(lift(user)).returning(u => u.userId)
      }
      ctx.run(q)
  }

  override def find(id: Int): Future[Option[User]] = {
    logger.debug("FIND")

    val q = quote {
      userTable.filter(u => u.userId.getOrElse(0) == lift(id))
    }

    val p = Promise[Option[User]]()
    val f = ctx.run(q)
    
    f.onComplete {
      case Success(list) => 
      logger.debug("FIND-SUCCESS")
      p success (list match {
        case Nil     =>  None
        case x :: xs =>  Some(x) 
      })
      case Failure(exception) => 
        logger.debug("FIND-FAILURE")
        logger.error(exception.getMessage())
        None
    }
    p.future
  }

  override def find(email: String): Future[Option[User]] = {
    val q = quote {
      userTable.filter(u => u.email == lift(email))
    }
    val p = Promise[Option[User]]()
    val f = ctx.run(q)
    
    f.onComplete {
      case Success(list) => p success (list match {
        case Nil     =>  None
        case x :: xs =>  Some(x) 
      })
      case Failure(_) => None
    }
    p.future
  }

  override def findAll: Future[List[User]] = ctx.run(query[User])

  override def delete(id: Int): Future[Boolean] = {
    val q = quote {
      userTable.filter(u => u.userId.getOrElse(0) == lift(id)).delete.returning(u => !u.userId.isEmpty)
    }
    ctx.run(q)
  }

  override def validate(email: String, password: String): Future[Option[User]] = {
    logger.debug("VALIDATE")
    val u = find(email)
    val p = Promise[Option[User]]()
    u.onComplete {
      case Success(value) => p success (value match {
        case None => 
          logger.debug("SUCCESS-NONE")
          None
        case Some(user) => 
          logger.debug("SUCCESS-SOME")
          logger.error(password)
          logger.error(user.passwd)
          logger.error(BCrypt.hashpw(password, BCrypt.gensalt(12)))
          if(BCrypt.checkpw(password, user.passwd))
            Some(user)
          else {
            logger.debug("CONTRASEÑA INCORRECTA")
            None
          }
      })
      case Failure(exception) => 
        logger.debug("FAILURE")
        logger.error(exception.getMessage())
        None
    }
    p.future
  }
}