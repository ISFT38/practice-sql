package ar.edu.isft38.bada.client

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import org.scalajs.dom
import slinky.web.html._
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global
import domain.User

@react class UserComponent extends Component {
  case class Props(user: User, changePassword: () => Unit, logged: (User) => Unit)
  case class State(logged: Boolean, user: User)

  def initialState: State = State(false, props.user)

  val url = dom.document.getElementById("logoutRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def updateUser(user: User): Unit = {
    setState(State(user != User.guest, user))
  }

  def getUser(): User = state.user

  def changePassword() { props.changePassword() }

  def logout() { 
  
    val headers = Map(
      "Content-Type" -> "application/json",
      "Csrf-Token"   -> csrfToken
    )
    
    val request = dom.ext.Ajax.post(
      url = url, 
      headers = headers,
      responseType = ""
    )
   
    request.foreach ( xhr =>  props.logged(User.guest) )

  }

  def render(): ReactElement = 
    div(className := "nav-item dropdown")(
      a(className := "nav-link dropdown-toggle", href := "#",
       id := "navbarDropdownMenuLink", role := "button", data-"toggle" := "dropdown",
        aria-"haspopup" := "true", aria-"expanded" := "false")(state.user.email),
      div(className := "dropdown-menu", aria-"labelledby" := "navbarDropdownMenuLink")(
        a(className := "dropdown-item", href := "#", hidden := !state.logged, onClick := (() => changePassword()))
          (span("Cambiar contraseña")),
        a(className := "dropdown-item", href := "#", hidden := !state.logged, onClick := (() => logout()))
          (span("Salir")),
        a(className := "dropdown-item", href := "#", hidden := (state.logged))
          (span("Ingrese para más opciones"))
      )
    )
}
