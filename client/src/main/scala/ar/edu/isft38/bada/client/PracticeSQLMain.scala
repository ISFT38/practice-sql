package ar.edu.isft38.bada.client

import shared.SharedMessages
import org.scalajs.dom

import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._
import domain.UserDTO
import slinky.core.facade.React

object PracticeSQLMain {

  val userComponentRef = React.createRef[UserComponent.Def]
  val userComponent = UserComponent(UserDTO(None, "Anónimo", true)).withRef(userComponentRef)

  def main(args: Array[String]): Unit = {
    ReactDOM.render(
      div(navigation, LoginComponent(logged)),
      dom.document.getElementById("root")
    )
  }

  def logged = (userDTO: UserDTO) => {
    userComponentRef.current.updateUser(userDTO)
  }

  def navigation = nav(className := "navbar navbar-expand-lg navbar-dark bg-dark")(
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
            a(className := "nav-link", href := "#")("Inicio")),
      li(className := "nav-item")(
            a(className := "nav-link", href := "#")("Ejercicios")),
      li(className := "nav-item")(
            a(className := "nav-link", href := "#")("Salir")),
      li(className := "nav-item")(
            a(className := "nav-link disabled", 
              href      := "#", 
        aria-"disabled" := "true")("Resultados"))
    ),
    div(className := "navbar-collapse collapse w-100 order-3 dual-collapse2")(userComponent))
  )
}
