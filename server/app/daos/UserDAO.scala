package daos

import daos.AbstractDAO
import domain.User
import scala.concurrent.Future

trait UserDAO extends AbstractDAO[User] {

  def find(email: String): Future[Option[User]]

  def validate(email: String, password: String): Future[Option[User]]
}