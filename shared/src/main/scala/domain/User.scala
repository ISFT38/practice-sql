package domain

import upickle.default.{ReadWriter => RW, macroRW}
import domain.Role
import shared.SharedMessages

final case class User(userId:    Option[Int], 
                      passwd:    String,
                      email:     String,
                      firstName: Option[String],
                      lastName:  Option[String],
                      confirmed: Boolean,
                      verified:  Boolean,
                      roles:     List[Role]) {


  def canCreate(user: User): Boolean = {
    !user.roles.contains(Role.Admin())      && 
    !user.roles.contains(Role.Guest())      &&
    (this.roles.contains(Role.Admin())      ||
    (this.roles.contains(Role.Professor())  && !user.roles.contains(Role.Professor())))
  }

}

object User {
  implicit val rw: RW[User] = macroRW

  val guest = User(None, "", "?", None, None, false, true, List[Role](Role.Guest()))
}
