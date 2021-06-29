package domain

import upickle.default.{ReadWriter => RW, macroRW}

sealed trait Role {
  def value: String
}

object Role {
  import upickle.default._

  case class Admin()     extends Role { val value = "admin" }
  case class Professor() extends Role { val value = "professor"}
  case class Student()   extends Role { val value = "student" }
  case class Guest()     extends Role { val value = "guest" }

  implicit val rw: RW[Role] = ReadWriter.merge(macroRW[Admin], 
                                               macroRW[Professor], 
                                               macroRW[Student],
                                               macroRW[Guest])

  def fromValue(value: String): Role = value match {
    case "admin"     => Admin()
    case "professor" => Professor()
    case "student"   => Student()
    case _           => Guest()
  }
}
