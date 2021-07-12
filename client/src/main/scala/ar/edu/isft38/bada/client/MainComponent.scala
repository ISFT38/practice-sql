package ar.edu.isft38.bada.client

import slinky.core.{Component, SyntheticEvent}
import slinky.core.annotations.react
import slinky.web.html._
import slinky.core.facade.ReactElement
import slinky.core.facade.React
import domain.User

@react class MainComponent extends Component {
  case class Props(messages: Map[String, String])
  case class State(user: User, guest: Boolean, 
                   login: Boolean, 
                   challenges: Boolean,
                   newChallenge: Boolean,
                   password: Boolean)

  def initialState: State = State(User.guest, true, true, false, false, false)

  def allOff(): Unit = setState(state.copy(login = false, 
                                           challenges = false, 
                                           password = false, 
                                           newChallenge = false))

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

  def exercises: () => Unit = () => {
    setState(state.copy(challenges = true, login = false, password = false, newChallenge = false))
  }

  def createChallenge: () => Unit = () => {
    setState(state.copy(newChallenge = true, challenges = false, login = false, password = false))
  }

  def render(): ReactElement = div(NavigationComponent(
                                    exercises, 
                                    home, 
                                    UserComponent(
                                      state.user,
                                      state.guest,
                                      changePassword,
                                      logged,
                                      props.messages),
                                    props.messages
                                    ), 
                                   LoginComponent(state.login, logged, props.messages),
                                   ChangePasswordComponent(state.password, props.messages), 
                                   CreateUserComponent(false, props.messages), 
                                   CreateChallengeComponent(state.newChallenge, props.messages),
                                   ChallengeListComponent(state.challenges, 10, createChallenge, props.messages))
}