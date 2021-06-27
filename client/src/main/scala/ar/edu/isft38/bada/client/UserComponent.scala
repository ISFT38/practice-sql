package ar.edu.isft38.bada.client

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import domain.UserDTO
import slinky.web.html._

@react class UserComponent extends Component {
  case class Props(user: UserDTO)
  case class State(logged: Boolean, user: UserDTO)

  def initialState: State = State(false, props.user)

  def updateUser(user: UserDTO): Unit = {
    setState(state.copy(user = user))
  }

  def render(): ReactElement = 
    div(className := "nav-item dropdown")(
      a(className := "nav-link dropdown-toggle", href := "#",
       id := "navbarDropdownMenuLink", role := "button", data-"toggle" := "dropdown",
        aria-"haspopup" := "true", aria-"expanded" := "false")(state.user.username),
      div(className := "dropdown-menu", aria-"labelledby" := "navbarDropdownMenuLink")(
        a(className := "dropdown-item", href := "#")(span("Perfil")),
        a(className := "dropdown-item", href := "#")(span("Ayuda")),
        a(className := "dropdown-item", href := "#")(span("Salir"))
      )
    )
}
