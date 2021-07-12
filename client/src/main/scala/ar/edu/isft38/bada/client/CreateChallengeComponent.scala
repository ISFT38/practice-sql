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
  case class Props(on: Boolean, messages: Map[String, String])
  case class State(question: String, database: String, query: String,
                   activate: Boolean, failure: Boolean, success: Boolean)

  def initialState: State = State("", "", "", false, false, false)

  implicit val ec = scala.concurrent.ExecutionContext.global

  val url = dom.document.getElementById("createChallengeRoute").asInstanceOf[dom.html.Input].value
  val csrfToken = dom.document.getElementById("csrfToken").asInstanceOf[dom.html.Input].value

  def clean() {
    setState(State("", "", "", false, true, false)) 
  }

  def updateQuestion(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(e.target.value, state.database, state.query,
                  (e.target.value == "" || state.database == ""),
                  false, false))

  def updateDatabase(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.question, e.target.value, state.query,
                  (state.question == "" || e.target.value == ""),
                  false, false))

  def updateQuery(e: SyntheticEvent[HTMLInputElement, org.scalajs.dom.Event]) =
    setState(State(state.question, state.database, e.target.value,
                  (state.question == "" || e.target.value == ""),
                  false, false))

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
          setState(state.copy(question = "", query = "", success = true))
        } else {
          setState(state.copy(failure = true))
        } 
      case Failure(exception) =>
        setState(state.copy(failure = true))
    }
  }

  def render(): ReactElement = main(className := "container", role := "main", hidden := !props.on)(
    h4(props.messages.get("challenge.create")),
    div( className := "form-group")(
      label(htmlFor := "database")(props.messages.get("database")),
      input(`type`              := "text",
            id                  := "database",
            className           := "form-control", 
            aria-"describedby"  := "databaseHelp", 
            placeholder         := props.messages.get("database.placeholder"),
            value               := state.database,
            onChange            := ((e) => updateDatabase(e))),
      small(id := "databaseHelp", className :="form-text text-muted")
           (props.messages.get("database.help")),     
    ),
    div( className := "form-group")(
      label(htmlFor := "question")(props.messages.get("question")),
      input(`type`              := "text",
            id                  := "question",
            className           := "form-control", 
            aria-"describedby"  := "questionHelp", 
            placeholder         := props.messages.get("question.placeholder"),
            value               := state.question,
            onChange            := ((e) => updateQuestion(e))),
      small(id := "questionHelp", className :="form-text text-muted")
           (props.messages.get("question.help"))
    ),
    div( className := "form-group")(
      label(htmlFor := "query")(props.messages.get("query")),
      input(`type`              := "text",
            id                  := "query",
            className           := "form-control", 
            aria-"describedby"  := "queryHelp", 
            placeholder         := props.messages.get("query.placeholder"),
            value               := state.query,
            onChange            := ((e) => updateQuery(e))),
      small(id := "questionHelp", className :="form-text text-muted")
           (props.messages.get("query.help"))
    ),
    div(
      button(className := "btn btn-outline-secondary",
             onClick := ((e) => clean()))(props.messages.get("button.clean")),
      span("   "),
      button(className := "btn btn-outline-success",
             onClick := ((e) => saveChallenge()), disabled := state.activate)(props.messages.get("challenge.create"))
    ),
    br(),
    div(className := "alert alert-danger",  role := "alert", hidden := !state.failure)(
      props.messages.get("challenge.error")
    ),
    div(className := "alert alert-success",  role := "alert", hidden := !state.success)(
      props.messages.get("challenge.success")
    )
  )
}
