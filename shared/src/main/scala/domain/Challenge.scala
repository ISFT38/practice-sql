package domain

import upickle.default.{ReadWriter => RW, macroRW}

final case class Challenge(challengeId: Option[Int], 
                           question:    String, 
                           dbase:       String,
                           answerQuery: String)

object Challenge {
  implicit val rw: RW[Challenge] = macroRW
}