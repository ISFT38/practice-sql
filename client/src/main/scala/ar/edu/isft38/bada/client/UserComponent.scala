package ar.edu.isft38.bada.client

import slinky.core.annotations.react
import slinky.core.StatelessComponent
import slinky.core.facade.ReactElement
import org.scalajs.dom
import slinky.web.html._
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global
import domain.User


@react class UserComponent extends StatelessComponent {
  case class Props(user: User, 
                   guest: Boolean, 
                   changePassword: () => Unit, 
                   logged: (User) => Unit,
                   messages: Map[String, String])

  val url = dom.document.getElementById("logoutRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def getUser(): User = props.user

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
        aria-"haspopup" := "true", aria-"expanded" := "false")(props.user.email),
      div(className := "dropdown-menu", aria-"labelledby" := "navbarDropdownMenuLink")(
        a(className := "dropdown-item", href := "#", hidden := props.guest, onClick := (() => changePassword()))
          (span(props.messages.get("password.change"))),
        a(className := "dropdown-item", href := "#", hidden := props.guest, onClick := (() => logout()))
          (span(props.messages.get("logout"))),
        a(className := "dropdown-item", href := "#", hidden := (!props.guest))
          (span(props.messages.get("login.options")))
      )
    )
}
