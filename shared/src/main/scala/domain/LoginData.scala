package domain

import upickle.default.{ReadWriter => RW, macroRW}

case class LoginData(id: Long, email: String, passwd: String, verified: Boolean)

object LoginData {
  implicit val rw: RW[LoginData] = macroRW
}