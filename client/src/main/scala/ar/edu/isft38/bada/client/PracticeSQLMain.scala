package ar.edu.isft38.bada.client

import org.scalajs.dom

import slinky.web.ReactDOM
import slinky.web.html._
import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default._
import scala.util.Success
import scala.util.Failure

object PracticeSQLMain {

  def main(args: Array[String]): Unit = {
    
    val request = dom.ext.Ajax.get("messages")
   
    request.onComplete {    
      case Success(xhr) =>
        ReactDOM.render(
          MainComponent(read[Map[String, String]](xhr.responseText)),
          dom.document.getElementById("root")
        )
      case Failure(exception) =>
        ReactDOM.render(
          MainComponent(Map.empty[String, String]),
          dom.document.getElementById("root")
        )
    }
  }
}
