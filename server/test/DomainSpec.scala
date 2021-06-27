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

  "User" should {
    "have a fullname Joe Doe" in {
      val user = User(None, None, "fake@email.com", Some("Joe"), Some("Doe"), false, false)
        
      user.username must beEqualTo( "Joe Doe" )
    }
    "have a fullname Joe" in {
      val user = User(None, None, "fake@email.com", Some("Joe"), None, false, false)
        
      user.username must beEqualTo( "Joe" )
    }
    "have a fullname Doe" in {
      val user = User(None, None, "fake@email.com", None, Some("Doe"), false, false)
        
      user.username must beEqualTo( "Doe" )
    }
    "have a fullname Anónimo" in {
      val user = User(None, None, "fake@email.com", None, None, false, false)
        
      user.username must beEqualTo( "Anónimo" )
    }
  }
}
