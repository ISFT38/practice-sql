package ar.edu.isft38.bada.client

import domain.User
import domain.LoginData
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLInputElement
import slinky.core.{Component, SyntheticEvent}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import scala.util.Success
import scala.util.Failure
import upickle.default._

@react class LoginComponent extends Component {
  case class Props(on: Boolean, logged: (User) => Unit, messages: Map[String, String])
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

  def data(): String = write[LoginData](LoginData(state.email, state.password))

  def login() {
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
        val user: Option[User] = read[Option[User]](xhr.responseText)
        user match {
          case None        => setState(state.copy(error = true))
          case Some(value) => 
            clean()
            props.logged(value)
        }
      case Failure(exception) =>
        setState(state.copy(failure = true))
    }
  }

  def render(): ReactElement = main(className := "container", role := "main", hidden := !props.on )(
    h4(props.messages.get("login")),
    div( className := "form-group")(
      label(htmlFor := "email")(props.messages.get("email.address")),
      input(`type`              := "email",
            id                  := "email",
            className           := "form-control", 
            aria-"describedby"  := "emailHelp", 
            placeholder         := props.messages.get("email.placeholder"),
            value               := state.email,
            onChange            := ((e) => updateEmail(e))),
      small(id := "emailHelp", className :="form-text text-muted")
           (props.messages.get("email.help"))
    ),
    div( className := "form-group")(
      label(htmlFor := "password")(props.messages.get("password")),
      input(`type`              := "password",
            id                  := "password",
            className           := "form-control", 
            aria-"describedby"  := "passwordHelp", 
            placeholder         := props.messages.get("password.placeholder"),
            value               := state.password,
            onChange            := ((e) => updatePassword(e))),
      small(id := "passwordHelp", className :="form-text text-muted")
           (props.messages.get("password.help")),     
    ),
    div(
      button(className := "btn btn-outline-secondary",
             onClick := ((e) => clean()))(props.messages.get("button.clean")),
      span("   "),
      button(className := "btn btn-outline-success",
             onClick := ((e) => login()), disabled := state.activate)(props.messages.get("login"))
    ),
    br(),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.error)(
      props.messages.get("password.wrong")
    ),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.failure)(
      props.messages.get("password.error")
    )
  )
}
