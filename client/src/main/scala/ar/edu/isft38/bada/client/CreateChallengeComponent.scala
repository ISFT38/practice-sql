package ar.edu.isft38.bada.client

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

import domain.Challenge

@react class CreateChallengeComponent extends Component {
  type Props = Unit
  case class State(question: String, database: String, query: String,
                   activate: Boolean, failure: Boolean, success: Boolean, on: Boolean)

  def initialState: State = State("", "", "", false, false, false, false)

  implicit val ec = scala.concurrent.ExecutionContext.global

  val url = dom.document.getElementById("createChallengeRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def clean() {
    setState(State("", "", "", false, true, false, false)) 
  }

  def setOn(value: Boolean) { setState(state.copy(on = value)) }

  def updateQuestion(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(e.target.value, state.database, state.query,
                  (e.target.value == "" || state.database == ""),
                  false, false, true))

  def updateDatabase(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.question, e.target.value, state.query,
                  (state.question == "" || e.target.value == ""),
                  false, false, true))

  def updateQuery(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.question, state.database, e.target.value,
                  (state.question == "" || e.target.value == ""),
                  false, false, true))

  def data(): String = write[Challenge](Challenge(None, state.question, state.database, state.query))

  def saveChallenge() {
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
        val id: Int = read[Int](xhr.responseText)
        if(id > 0) {
          setState(state.copy(question = "", success = true))
        } else {
          setState(state.copy(failure = true))
        } 
      case Failure(exception) =>
        setState(state.copy(failure = true))
    }
  }

  def render(): ReactElement = main(className := "container", role := "main", hidden := !state.on)(
    h4("Crear desafío"),
    div( className := "form-group")(
      label(htmlFor := "database")("Base de datos"),
      input(`type`              := "text",
            id                  := "database",
            className           := "form-control", 
            aria-"describedby"  := "databaseHelp", 
            placeholder         := "Ingrese el nombre de la base de datos",
            value               := state.database,
            onChange            := ((e) => updateDatabase(e))),
      small(id := "databaseHelp", className :="form-text text-muted")
           ("La base de datos en la que hay que hacer la consulta."),     
    ),
    div( className := "form-group")(
      label(htmlFor := "question")("Enunciado"),
      input(`type`              := "text",
            id                  := "question",
            className           := "form-control", 
            aria-"describedby"  := "questionHelp", 
            placeholder         := "Ingrese el enunciado",
            value               := state.question,
            onChange            := ((e) => updateQuestion(e))),
      small(id := "questionHelp", className :="form-text text-muted")
           ("La pregunta que se le mostrará al estudiante.")
    ),
    div( className := "form-group")(
      label(htmlFor := "query")("Consulta"),
      input(`type`              := "text",
            id                  := "query",
            className           := "form-control", 
            aria-"describedby"  := "queryHelp", 
            placeholder         := "Ingrese la consulta",
            value               := state.query,
            onChange            := ((e) => updateQuery(e))),
      small(id := "questionHelp", className :="form-text text-muted")
           ("La consulta a la base de datos que tiene que escribir el estudiante.")
    ),
    div(
      button(className := "btn btn-outline-secondary",
             onClick := ((e) => clean()))("Limpiar"),
      span("   "),
      button(className := "btn btn-outline-success",
             onClick := ((e) => saveChallenge()), disabled := state.activate)("Crear desafío")
    ),
    br(),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.failure)(
      "Hubo un error al intentar crear el desafío, por favor intente nuevamente."
    ),
    div(className := "alert alert-success",  role := "alert", hidden := !state.success)(
      "Desafío grabado correctamente."
    )
  )
}
