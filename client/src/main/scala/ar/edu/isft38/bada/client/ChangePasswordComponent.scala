package ar.edu.isft38.bada.client

import domain.ChangePasswordDTO
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

@react class ChangePasswordComponent extends Component {
  case class Props(on: Boolean, messages: Map[String, String])
  case class State(oldPassword: String, password: String, passConf: String, distinct: Boolean,
                   activate: Boolean, error: Boolean, failure: Boolean, success: Boolean)

  def initialState: State = State("", "", "", false, true, false, false, false)

  implicit val ec = scala.concurrent.ExecutionContext.global

  val url = dom.document.getElementById("changePasswordRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def clean() {
    setState(State("", "", "", false, true, false, false, false)) 
  }

  def updateOldPassword(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(e.target.value, state.password, state.passConf, state.password != state.passConf,
                  (e.target.value == "" || state.password == "" || state.passConf == "" || state.password == state.passConf),
                  false, false, false))

  def updatePassword(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.oldPassword, e.target.value, state.passConf,  e.target.value != state.passConf,
                  (state.oldPassword == "" || e.target.value == "" || e.target.value != state.passConf),
                  false, false, false))

  def updatePassConf(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.oldPassword, state.password, e.target.value,  state.password != e.target.value,
                  (state.oldPassword == "" || e.target.value == "" || state.password != e.target.value),
                  false, false, false))


  def data(): String = write[ChangePasswordDTO](ChangePasswordDTO(state.oldPassword, state.password))

  def changePassword() {
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
        val s: Boolean = read[Boolean](xhr.responseText)
        if(s) {
          clean
          setState(state.copy(success = true))
        } else {
          setState(state.copy(error = true))
        } 
      case Failure(exception) =>
        setState(state.copy(failure = true))
    }
  }

  def render(): ReactElement = main(className := "container", role := "main", hidden := !props.on)(
    h4(props.messages.get("password.change")),
    div( className := "form-group")(
      label(htmlFor := "email")(props.messages.get("password.old")),
      input(`type`              := "password",
            id                  := "oldPassword",
            className           := "form-control", 
            aria-"describedby"  := "contraHelp", 
            placeholder         := props.messages.get("password.old.placeholder"),
            value               := state.oldPassword,
            onChange            := ((e) => updateOldPassword(e))),
      small(id := "contraHelp", className :="form-text text-muted")
           (props.messages.get("password.old.help"))
    ),
    div( className := "form-group")(
      label(htmlFor := "password")(props.messages.get("password.new")),
      input(`type`              := "password",
            id                  := "password",
            className           := "form-control", 
            aria-"describedby"  := "passwordHelp", 
            placeholder         := props.messages.get("password.new.placeholder"),
            value               := state.password,
            onChange            := ((e) => updatePassword(e))),
      small(id := "passwordHelp", className :="form-text text-muted")
           (props.messages.get("password.help")),     
    ),
    div( className := "form-group")(
      label(htmlFor := "passConf")(props.messages.get("password.confirmation")),
      input(`type`              := "password",
            id                  := "passConf",
            className           := "form-control", 
            aria-"describedby"  := "passConfHelp", 
            placeholder         := props.messages.get("password.confirmation.placeholder"),
            value               := state.passConf,
            onChange            := ((e) => updatePassConf(e))),
      small(id := "passConfHelp", className :="form-text text-muted")
           (props.messages.get("password.help")),     
    ),
    div(
      button(className := "btn btn-outline-secondary",
             onClick := ((e) => clean()))(props.messages.get("button.clean")),
      span("   "),
      button(className := "btn btn-outline-success",
             onClick := ((e) => changePassword()), disabled := state.activate)(props.messages.get("password.change"))
    ),
    br(),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.error)(
      props.messages.get("password.wrong")
    ),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.failure)(
      props.messages.get("password.error")
    ),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.distinct)(
      props.messages.get("password.match")
    )
  )
}
