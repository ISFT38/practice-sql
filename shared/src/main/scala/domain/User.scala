package domain

import upickle.default.{ReadWriter => RW, macroRW}
import domain.Role

case class User(userId:    Option[Int], 
                passwd:    String,
                email:     String,
                firstName: Option[String],
                lastName:  Option[String],
                confirmed: Boolean,
                verified:  Boolean,
                roles:     List[Role]) {

}

object User {
  implicit val rw: RW[User] = macroRW
}
