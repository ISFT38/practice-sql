package domain

import upickle.default.{ReadWriter => RW, macroRW}

case class ChangePasswordDTO(oldPassword: String, newPassword: String)

object ChangePasswordDTO {
  implicit val rw: RW[ChangePasswordDTO] = macroRW
}