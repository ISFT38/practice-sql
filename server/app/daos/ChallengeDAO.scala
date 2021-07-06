package daos

import domain.Challenge
import scala.concurrent.Future

trait ChallengeDAO extends AbstractDAO[Challenge] {
  def find (pageSize: Int, page: Int): Future[Seq[Challenge]]
}