import org.junit.runner._
import org.specs2.runner._
import play.api.test._
import org.specs2.mutable.Specification
import domain.User

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class DomainSpec extends Specification {
/* domain.User simplified the email will be used as username
  "User" should {
    "have a fullname joe.doe" in {
      val user = User(None, "", "fake@email.com", Some("Joe"), Some("Doe"), false, false, Nil)
        
      user.username must beEqualTo( "joe.doe" )
    }
    "have a fullname joe" in {
      val user = User(None, "", "fake@email.com", Some("Joe"), None, false, false, Nil)
        
      user.username must beEqualTo( "joe" )
    }
    "have a fullname doe" in {
      val user = User(None, "", "fake@email.com", None, Some("Doe"), false, false, Nil)
        
      user.username must beEqualTo( "doe" )
    }
    "have a fullname anónimo" in {
      val user = User(None, "", "fake@email.com", None, None, false, false, Nil)
        
      user.username must beEqualTo( "anónimo" )
    }
  }
  */
}
