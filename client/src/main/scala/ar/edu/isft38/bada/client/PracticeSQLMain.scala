package ar.edu.isft38.bada.client

import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._
import slinky.core.facade.React

import scala.util.Success
import scala.util.Failure
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax

import domain.User
import shared.SharedMessages
import domain.Role

object PracticeSQLMain {

  val guest = User(None, "", "Anónimo", None, None, false, true, List[Role](Role.Guest()))

  val userComponentRef           = React.createRef[UserComponent.Def]
  val loginComponentRef          = React.createRef[LoginComponent.Def]
  val changePasswordComponentRef = React.createRef[ChangePasswordComponent.Def]

  val userComponent = UserComponent(guest, 
                                    changePassword,
                                    logged).withRef(userComponentRef)
  
  val changePasswordComponent = ChangePasswordComponent(true).withRef(changePasswordComponentRef)
  val loginComponent = LoginComponent(logged).withRef(loginComponentRef)

  def main(args: Array[String]): Unit = {
    ReactDOM.render(
      div(navigation, loginComponent, changePasswordComponent),
      dom.document.getElementById("root")
    )
  }

  def changePassword: () => Unit = () => {
    loginComponentRef.current.setOn(false)
    changePasswordComponentRef.current.setOn(true)
  }

  def logged = (user: User) => {
    userComponentRef.current.updateUser(user)
  }

  def navigation = nav(className := "navbar navbar-expand-lg navbar-dark bg-dark")(
    a(className := "navbar-brand", href := "#")("Practice-SQL"),
    button(className       := "navbar-toggler", 
           `type`          := "button", 
           data-"toggle"   := "collapse", 
           data-"target"   := "#navbarNav",
           aria-"controls" := "navbarNav",
           aria-"expanded" := "false",
           aria-"label"    := "Toggle navigation",
           span(className := "navbar-toggler-icon")),    
    div(className := "collapse navbar-collapse", id := "navbarNav")(
    ul(className := "navbar-nav")(
      li(className := "nav-item active")(
            a(className := "nav-link", href := "#")("Inicio")),
      li(className := "nav-item")(
            a(className := "nav-link", href := "#")("Ejercicios")),
      li(className := "nav-item")(
            a(className := "nav-link disabled", 
              href      := "#", 
        aria-"disabled" := "true")("Resultados"))
    ),
    div(className := "navbar-collapse collapse w-100 order-3 dual-collapse2")(userComponent))
  )
}
