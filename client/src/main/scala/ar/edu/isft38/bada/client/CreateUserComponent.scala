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
import domain.User

@react class CreateUserComponent extends Component {
  case class Props(on: Boolean)
  case class State(firstName: String, lastName: String, email: String, 
                   activate: Boolean, error: Boolean, admin: Boolean,
                   failure: Boolean, success: Boolean)

  def initialState: State = State("", "", "", false, false, false, 
                                  false, false)

  implicit val ec = scala.concurrent.ExecutionContext.global

  val url = dom.document.getElementById("createUserRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def clean() {
    setState(State("", "", "", false, false, false, false, false)) 
  }
  
/*
  def updateFirstName(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(e.target.value, state.password, state.passConf, state.password != state.passConf,
                  (e.target.value == "" || state.password == "" || state.passConf == "" || state.password == state.passConf),
                  false, false, false, true))

  def updatePassword(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.oldPassword, e.target.value, state.passConf,  e.target.value != state.passConf,
                  (state.oldPassword == "" || e.target.value == "" || e.target.value != state.passConf),
                  false, false, false, true))

  def updatePassConf(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.oldPassword, state.password, e.target.value,  state.password != e.target.value,
                  (state.oldPassword == "" || e.target.value == "" || state.password != e.target.value),
                  false, false, false, true))


  def data(): String = write[ChangePasswordDTO](ChangePasswordDTO(state.oldPassword, state.password))

  def changePassword() {
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
*/
  def render(): ReactElement = main(className := "container", role := "main", hidden := !props.on)(
    h4("Crear usuario"))/*,
    div( className := "form-group")(
      label(htmlFor := "firs_name")("Nombre"),
      input(`type`              := "text",
            id                  := "first_name",
            className           := "form-control", 
            aria-"describedby"  := "firstHelp", 
            placeholder         := "Ingrese el nombre del nuevo usuario",
            value               := state.firstName,
            onChange            := ((e) => updateFirstName(e))),
      small(id := "firstHelp", className :="form-text text-muted")
           ("El nombre de pila del usuario que quiere crear en el sistema.")
    ),
    div( className := "form-group")(
      label(htmlFor := "password")("Nueva contraseña"),
      input(`type`              := "password",
            id                  := "password",
            className           := "form-control", 
            aria-"describedby"  := "passwordHelp", 
            placeholder         := "Ingrese la nueva contraseña",
            value               := state.password,
            onChange            := ((e) => updatePassword(e))),
      small(id := "passwordHelp", className :="form-text text-muted")
           ("Al menos 8 caracteres."),     
    ),
    div( className := "form-group")(
      label(htmlFor := "passConf")("Nueva contraseña"),
      input(`type`              := "password",
            id                  := "passConf",
            className           := "form-control", 
            aria-"describedby"  := "passConfHelp", 
            placeholder         := "Repita la nueva contraseña",
            value               := state.passConf,
            onChange            := ((e) => updatePassConf(e))),
      small(id := "passConfHelp", className :="form-text text-muted")
           ("Al menos 8 caracteres."),     
    ),
    div(
      button(className := "btn btn-outline-secondary",
             onClick := ((e) => clean()))("Limpiar"),
      span("   "),
      button(className := "btn btn-outline-success",
             onClick := ((e) => changePassword()), disabled := state.activate)("Cambiar contraseña")
    ),
    br(),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.error)(
      "La contraseña actual es incorrecta."
    ),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.failure)(
      "Hubo un error al intentar cambiar la contraseña, por favor intente nuevamente."
    ),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.distinct)(
      "La nueva contraseña y la confirmación no coinciden."
    )
  )
  */
}
