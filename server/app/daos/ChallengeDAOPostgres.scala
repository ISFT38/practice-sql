package daos

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import daos.ChallengeDAO
import domain.Challenge
import scala.concurrent.Promise
import scala.util.Success
import scala.util.Failure

class ChallengeDAOPostgres extends ChallengeDAO {

  import db.ctx
  import db.ctx._

  val challengeTable = quote { querySchema[Challenge]("challenge") }

  def save(entity: Challenge): Future[Option[Int]] = entity.challengeId match {
    case None => 
      val q = quote {
        challengeTable.insert(lift(entity)).returningGenerated(_.challengeId)
      }

      ctx.run(q)

    case Some(value) => 
      val q = quote {
        challengeTable.filter(_.challengeId.getOrElse(0) == lift(value)).update(lift(entity)).returning(_.challengeId)
      }

      ctx.run(q)
  }

  def find(id: Int): Future[Option[Challenge]] = {
    val q = quote {
      challengeTable.filter(c => c.challengeId.getOrElse(0) == lift(id))
    }

    val p = Promise[Option[Challenge]]()
    val f = ctx.run(q)
    
    f.onComplete {
      case Success(list) => 
      p success (list match {
        case Nil     =>  None
        case x :: xs =>  Some(x) 
      })
      case Failure(exception) => 
        None
    }
    p.future
  }

  def findAll: Future[List[Challenge]] = ctx.run(challengeTable)

  def delete(id: Int): Future[Boolean] = {
    val q = quote {
      challengeTable.filter(c => c.challengeId.getOrElse(0) == lift(id)).delete.returning(c => !c.challengeId.isEmpty)
    }
    ctx.run(q)
  }
}