package ar.edu.isft38.bada.client

import domain.UserDTO
import domain.LoginData
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.{Component, SyntheticEvent}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
//import io.scalajs. // FIXME continuar

//import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default._
import scala.util.Success
import scala.util.Failure


@react class LoginComponent extends Component {
  case class Props(logged: (UserDTO) => Unit)
  case class State(email: String, password: String, 
                   activate: Boolean, error: Boolean, failure: Boolean)

  def initialState: State = State("", "", true, false, false)

  implicit val ec = scala.concurrent.ExecutionContext.global

  val url = dom.document.getElementById("loginRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def clean() {
    setState(State("", "", true, false, false)) 
  }

  def updateEmail(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(e.target.value, state.password, (e.target.value == "" || state.password == ""), false, false))

  def updatePassword(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.email, e.target.value, (state.email == "" || e.target.value == ""), false, false))

  def data(): String = write[LoginData](LoginData(0, state.email, state.password, false))

  def login() {
    println("Click en ingresar")
    val headers = Map(
      "Content-Type" -> "application/json",
      "Csrf-Token"   -> csrfToken
    )
    
    val request = Ajax.post(
      url = url, 
      data = Ajax.InputData.str2ajax(data()),
      headers = headers,
      responseType = ""
    )
   
    request.onComplete {    
      case Success(xhr) =>
        val user: Option[UserDTO] = read[Option[UserDTO]](xhr.responseText)
        user match {
          case None        => setState(state.copy(error = true))
          case Some(value) => props.logged(value)
        }
      case Failure(exception) =>
        setState(state.copy(failure = true))
    }
  }

  def render(): ReactElement = main(className := "container", role := "main")(
    h4("Ingresar"),
    div( className := "form-group")(
      label(htmlFor := "email")("Dirección de email"),
      input(`type`              := "email",
            id                  := "email",
            className           := "form-control", 
            aria-"describedby"  := "emailHelp", 
            placeholder         := "Ingresar el email",
            value               := state.email,
            onChange            := ((e) => updateEmail(e))),
      small(id := "emailHelp", className :="form-text text-muted")
           ("El mismo email que el usado en el campus.")
    ),
    div( className := "form-group")(
      label(htmlFor := "password")("Contraseña"),
      input(`type`              := "password",
            id                  := "password",
            className           := "form-control", 
            aria-"describedby"  := "passwordHelp", 
            placeholder         := "Ingresar la contraseña",
            value               := state.password,
            onChange            := ((e) => updatePassword(e))),
      small(id := "passwordHelp", className :="form-text text-muted")
           ("Al menos 8 caracteres."),     
    ),
    div(
      button(className := "btn btn-outline-secondary",
             onClick := ((e) => clean()))("Limpiar"),
      span("   "),
      button(className := "btn btn-outline-success",
             onClick := ((e) => login()), disabled := state.activate)("Ingresar")
    ),
    br(),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.error)(
      "Usuario o contraseña incorrectos."
    ),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.failure)(
      "Hubo un error al intentar validar la identidad, por favor intente nuevamente."
    )
  )
}
