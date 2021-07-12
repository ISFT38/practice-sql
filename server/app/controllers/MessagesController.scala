package controllers

import javax.inject.Inject
import play.api.i18n._
import play.api.mvc.Action
import play.api.mvc.MessagesRequest
import play.api.mvc.MessagesControllerComponents
import play.api.mvc.MessagesAbstractController
import play.api.mvc.AnyContent
import scala.annotation.tailrec
import upickle.default._

class MessagesController @Inject() (mcc: MessagesControllerComponents) extends MessagesAbstractController(mcc) {

  val allMessages = messagesApi.messages

  def bestLanguage(implicit request: MessagesRequest[AnyContent]): String = {

    @tailrec
    def loop(languages: Seq[Lang]): Lang = {
      if(languages.isEmpty) 
        Lang("us") // Defaults to us
      else if (allMessages.keySet.contains(languages.head.code))
        languages.head
      else 
        loop(languages.tail)

    }
    val langs = request.acceptLanguages
    val lang  = request.transientLang()

    lang match {
      case None => loop(langs).code
      case Some(value) => 
        if(allMessages.keySet.contains(value.code))
          value.code
        else
          loop(langs).code
    }
  }

  def messages = Action { implicit request: MessagesRequest[AnyContent] =>
    
     Ok(write[Map[String, String]](allMessages.get(bestLanguage).get))
  }

}