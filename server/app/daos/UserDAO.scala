package daos

import domain.User
import scala.concurrent.Future
import domain.UserDTO

trait UserDAO {

  def save(user: User): Future[Option[Int]]

  def find(id: Int): Future[Option[User]]

  def find(email: String): Future[Option[User]]

  def findAll: Future[List[User]]

  def delete(id: Int): Future[Boolean]

  def validate(email: String, password: String): Future[Option[UserDTO]]
}