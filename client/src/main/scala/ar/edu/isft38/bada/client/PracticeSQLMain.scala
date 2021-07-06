package ar.edu.isft38.bada.client

import org.scalajs.dom

import slinky.web.ReactDOM
import slinky.web.html._

object PracticeSQLMain {

  def main(args: Array[String]): Unit = {
    ReactDOM.render(
      MainComponent(),
      dom.document.getElementById("root")
    )
  }
}
