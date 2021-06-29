package domain

import upickle.default.{ReadWriter => RW, macroRW}
import domain.Role

case class UserDTO(userId:         Option[Int],
                   username:       String,
                   changePassword: Boolean)

object UserDTO {
  implicit val rw: RW[UserDTO] = macroRW
}

case class User(userId:    Option[Int], 
                passwd:    String,
                email:     String,
                firstName: Option[String],
                lastName:  Option[String],
                confirmed: Boolean,
                verified:  Boolean,
                roles:     List[Role]) {

  val toUserDto: UserDTO = UserDTO(userId, email, verified)
}

object User {
  implicit val rw: RW[User] = macroRW
}
