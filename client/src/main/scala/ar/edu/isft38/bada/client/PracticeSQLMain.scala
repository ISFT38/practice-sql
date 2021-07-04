package ar.edu.isft38.bada.client

import slinky.core.facade.React
import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._

import scala.util.Success
import scala.util.Failure
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax

import domain.User
import domain.Role
import shared.SharedMessages

object PracticeSQLMain {

  val userComponentRef            = React.createRef[UserComponent.Def]
  val loginComponentRef           = React.createRef[LoginComponent.Def]
  val changePasswordComponentRef  = React.createRef[ChangePasswordComponent.Def]
  val createChallengeComponentRef = React.createRef[CreateChallengeComponent.Def]

  val userComponent = UserComponent(User.guest, 
                                    changePassword,
                                    logged).withRef(userComponentRef)
  
  val changePasswordComponent = ChangePasswordComponent().withRef(changePasswordComponentRef)
  val loginComponent = LoginComponent(logged).withRef(loginComponentRef)
  val createUserComponent = CreateUserComponent(true)
  val createChallengeComponent = CreateChallengeComponent().withRef(createChallengeComponentRef)
  val navigationComponent = NavigationComponent(exercises, home, userComponent)

  def main(args: Array[String]): Unit = {
    ReactDOM.render(
      div(navigationComponent, loginComponent, 
                      changePasswordComponent, 
                      createUserComponent, 
                      createChallengeComponent),
      dom.document.getElementById("root")
    )
  }

  def allOff(): Unit = {
    loginComponentRef.current.setOn(false)
    changePasswordComponentRef.current.setOn(false)
    createChallengeComponentRef.current.setOn(false)
  }

  def home: () => Unit = () => {
    allOff()
    if(userComponentRef.current.getUser() == User.guest) {
      loginComponentRef.current.setOn(true)
    } else {
      createChallengeComponentRef.current.setOn(true)
    }
  }

  def changePassword: () => Unit = () => {
    allOff()
    changePasswordComponentRef.current.setOn(true)
  }

  def logged = (user: User) => {
    userComponentRef.current.updateUser(user)
    home()
  }

  def exercises(): () => Unit = () => {
    allOff()
    createChallengeComponentRef.current.setOn(true)
  }

}
