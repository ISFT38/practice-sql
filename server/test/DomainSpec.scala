import org.junit.runner._
import org.specs2.runner._
import play.api.test._
import org.specs2.mutable.Specification
import domain.User
import domain.Role

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class DomainSpec extends Specification {
  val professor = User(None, "", "fake@email.com", 
                      None, None, false, false, List(Role.Professor()))
  val admin     = User(None, "", "fake@email.com", 
                      None, None, false, false, List(Role.Admin()))
  val guest     = User(None, "", "fake@email.com", 
                      None, None, false, false, List(Role.Guest()))
  val student   = User(None, "", "fake@email.com", 
                      None, None, false, false, List(Role.Student()))
  val professorGuest = User(None, "", "fake@email.com", 
                      None, None, false, false, List(Role.Professor(), Role.Guest()))
  val professorStudent = User(None, "", "fake@email.com", 
                      None, None, false, false, List(Role.Professor(), Role.Student()))
                      
  
  "User" should {
    "never create a Guest user" in { !admin.canCreate(guest) }
    "never create a Guest user with other role" in { !admin.canCreate(professorGuest) }
    "never create a Admin user" in { !admin.canCreate(admin) }
    "never create a user if he is a Student" in { !student.canCreate(student) }
    "never create a Professor if he is a Professor" in { !professor.canCreate(professor) }
    "never create a Professor if he is a Professor" in { !professor.canCreate(professorStudent) }
    "have role admin if he is Admin" in { admin.roles.contains(Role.Admin())}
    "create Professors if it is Admin" in { admin.canCreate(professor) }
    
  }
}
