package domain

import upickle.default.{ReadWriter => RW, macroRW}

case class LoginData(email: String, passwd: String)

object LoginData {
  implicit val rw: RW[LoginData] = macroRW
}