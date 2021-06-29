import javax.inject.Inject
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import daos.UserDAOPostgres
import domain.User

@RunWith(classOf[JUnitRunner])
class DatabaseIntegrationSpec extends Specification {
  val dao = new UserDAOPostgres

  val fake: User = User(Some(1), 
                        "fake_pass", 
                        "fake@email.com", 
                        Some("Joe"), 
                        Some("Doe"), false, false, Nil)

  val sample: User = User(None, 
                          "plainText", 
                          "sample@email.com", 
                          Some("Alice"), 
                          Some("Rol"), false, false, Nil)

  "Application" should {
    "insert a User to Database" in {
      dao.save(sample).map(user =>
      user match {
        case None => false
        case Some(u) => true
      })
    }
    "get a User from Database" in {
      val user = dao.find(1)
      Await.result(user.map(_.getOrElse(sample) === fake), 10.second)
    }
    "delete a User from Database" in {
      for {
        u <- dao.find("sample@email.com")
        d <- u match {
          case None       => dao.delete(0)
          case Some(user) => dao.delete(user.userId.getOrElse(0))
        } 
      } yield d
    }
  }
}
