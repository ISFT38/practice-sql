package ar.edu.isft38.bada.client

import scala.concurrent.ExecutionContext.Implicits.global
import slinky.core.annotations.react
import slinky.core.Component
import slinky.web.html._
import slinky.core.facade.ReactElement
import domain.Challenge
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import scala.tools.nsc.interpreter.Results
import scala.util.Success
import upickle.default._
import scala.util.Failure

@react class ChallengeListComponent extends Component {
  case class Props(on: Boolean, pageSize: Int, createChallenge: () => Unit, messages: Map[String, String])
  case class State(page: Int, total: Int, challenges: Seq[Challenge], failure: Boolean)

  def initialState: State = State(0, 0, Nil, false)

  def challengesUpdate() {
    println("METH")
    val request = Ajax.get(s"challenges/${props.pageSize}/${state.page}")
   
    request.onComplete {    
      case Success(xhr) =>
        setState(state.copy(challenges = read[Seq[Challenge]](xhr.responseText)))
      case Failure(exception) =>
        setState(state.copy(failure = true))
    }
  }

  def delete(idOpt: Option[Int]) { 
    idOpt.map { id =>

      val request = Ajax.get(s"delete-challenge/$id")    
      
      request.onComplete {    
        case Success(xhr) =>
          challengesUpdate()
        case Failure(exception) =>
          setState(state.copy(failure = true))
      }
    }
  }

  def edit(id: Option[Int]) {

  }

  def send(id: Option[Int]) {

  }

  def create() {
    props.createChallenge()
  }

  override def componentDidMount(): Unit = challengesUpdate()

  def render(): ReactElement = div(hidden := !props.on)(
    h4(props.messages.get("challenge.list")),
    table(className := "table table-stripped")(
      thead(tr(
        th(scope := "col-sm-9")(props.messages.get("question")), 
        th(scope := "col-sm-3")(props.messages.get("title.actions")))),
      tbody(
      state.challenges.map { challenge =>
        tr(key := challenge.challengeId.map(_.toString))(
        td(challenge.question), td(
          button(`type` := "button", className := "btn btn-outline-dark", 
                 onClick := ((e) => delete(challenge.challengeId)))("X"), 
          button(`type` := "button", className := "btn btn-outline-dark",
                 onClick := ((e) => edit(challenge.challengeId)))("✎"), 
          button(`type` := "button", className := "btn btn-outline-dark",
                 onClick := ((e) => send(challenge.challengeId)))("⇒")))
      })
    ),
    button(`type` := "button", className := "btn btn-outline-dark", 
                 onClick := ((e) => create()))("+"), // TODO load create challenge
    span(" "),
    button(`type` := "button", className := "btn btn-outline-dark", 
                 onClick := ((e) => {}))("<"), // TODO show previous page
    button(`type` := "button", className := "btn btn-outline-dark", 
                 onClick := ((e) => {}))(">") // TODO show next page
  )

}