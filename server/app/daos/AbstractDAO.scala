package daos

import scala.concurrent.Future

trait AbstractDAO[A] {

  def save(entity: A): Future[Option[Int]]

  def find(id: Int): Future[Option[A]]

  def findAll: Future[List[A]]

  def delete(id: Int): Future[Boolean]
}