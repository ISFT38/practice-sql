package domain

import upickle.default.{ReadWriter => RW, macroRW}

case class UserDTO(userId:         Option[Int],
                   username:       String,
                   changePassword: Boolean)

object UserDTO {
  implicit val rw: RW[UserDTO] = macroRW
}

case class User(userId:    Option[Int], 
                passwd:    Option[String],
                email:     String,
                firstName: Option[String],
                lastName:  Option[String],
                confirmed: Boolean,
                verified:  Boolean) {

  val username: String = (firstName, lastName) match {
    case (Some(f), Some(l)) => s"$f $l"
    case (Some(f), None)    => f
    case (None, Some(l))    => l
    case (None, None)       => "Anónimo"
  }

  val toUserDto: UserDTO = UserDTO(userId, username, verified)
}