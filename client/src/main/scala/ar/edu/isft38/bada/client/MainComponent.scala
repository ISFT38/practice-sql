package ar.edu.isft38.bada.client

import slinky.core.{Component, SyntheticEvent}
import slinky.core.annotations.react
import slinky.web.html._
import slinky.core.facade.ReactElement
import slinky.core.facade.React
import domain.User

@react class MainComponent extends Component {
  type Props = Unit
  case class State(user: User, guest: Boolean, 
                   login: Boolean, 
                   challenges: Boolean, 
                   password: Boolean)

  def initialState: State = State(User.guest, true, true, false, false)

  def allOff(): Unit = setState(state.copy(login = false, challenges = false, password = false))

  def home: () => Unit = () => {
    allOff()
    if(state.user == User.guest) {
      setState(state.copy(login = true))
    } else {
      setState(state.copy(challenges = true))
    }
  }

  def changePassword: () => Unit = () => {
    allOff()
    setState(state.copy(password = true))
  }

  def logged = (user: User) => {
    setState(state.copy(user = user, guest = user == User.guest))
    home()
  }

  def exercises(): () => Unit = () => {
    allOff()
    setState(state.copy(challenges = true))
  }

  def render(): ReactElement = div(NavigationComponent(
                                    exercises, 
                                    home, 
                                    UserComponent(
                                      state.user,
                                      state.guest,
                                      changePassword,
                                      logged)
                                    ), 
                                   LoginComponent(state.login, logged), 
                                   ChangePasswordComponent(state.password), 
                                   CreateUserComponent(false), 
                                   CreateChallengeComponent(false),
                                   ChallengeListComponent(state.challenges, 10))
}