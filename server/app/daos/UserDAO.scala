package daos

import domain.User
import scala.concurrent.Future

trait UserDAO {

  def save(user: User): Future[Option[Int]]

  def find(id: Int): Future[Option[User]]

  def find(email: String): Future[Option[User]]

  def findAll: Future[List[User]]

  def delete(id: Int): Future[Boolean]

  def validate(email: String, password: String): Future[Option[User]]
}