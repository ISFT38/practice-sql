package domain

import upickle.default.{ReadWriter => RW, macroRW}

final case class ChangePasswordDTO(oldPassword: String, newPassword: String)

object ChangePasswordDTO {
  implicit val rw: RW[ChangePasswordDTO] = macroRW
}