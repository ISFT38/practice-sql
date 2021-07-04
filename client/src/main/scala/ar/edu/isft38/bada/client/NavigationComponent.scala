package ar.edu.isft38.bada.client

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class NavigationComponent extends Component {
  case class Props(exercises: () => Unit, home: () => Unit, userComponent: ReactElement)
  case class State(admin: Boolean, professor: Boolean, student: Boolean)

  def initialState: State = State(false, false, false)

  def render(): ReactElement = nav(className := "navbar navbar-expand-lg navbar-dark bg-dark")(
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
            a(className := "nav-link", href := "#", onClick := props.home)("Inicio")),
      li(className := "nav-item")(
            a(className := "nav-link", href := "#", onClick := props.exercises)("Ejercicios")),
      li(className := "nav-item")(
            a(className := "nav-link disabled", 
              href      := "#",
        aria-"disabled" := "true")("Resultados"))
    ),
    div(className := "navbar-collapse collapse w-100 order-3 dual-collapse2")(props.userComponent))
  )
}