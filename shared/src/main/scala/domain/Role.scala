package domain

import upickle.default.{ReadWriter => RW, macroRW}

sealed trait Role {
  def value = this match {
    case Admin     => "admin"
    case Professor => "professor"
    case Student   => "student"
  }

  implicit val rw: RW[Role] = macroRW
}
object Admin     extends Role
object Professor extends Role
object Student   extends Role