import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._
import javax.inject.Inject
import daos.UserDAO
import domain.User
//import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import daos.UserDAOPostgres
import scala.concurrent.duration._
import scala.concurrent.Await

@RunWith(classOf[JUnitRunner])
class DatabaseIntegrationSpec extends Specification {
  val dao = new UserDAOPostgres

  val fake: User = User(Some(1), 
                        Some("fake_pass"), 
                        "fake@email.com", 
                        Some("Joe"), 
                        Some("Doe"), false, false)
  val sample: User = User(None, Some("plainText"), "sample@email.com", 
                          Some("Alice"), Some("Rol"), false, false)
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
      Await.result(user.map(_.get === fake), 10.second)
    }
  }
}
