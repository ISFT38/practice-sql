package init

import scala.io._
import daos.UserDAO
import daos.UserDAOPostgres
import domain.User
import domain.Role
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.annotation.tailrec

object Init {

  def ask(question: String): String = {
    print(question)
    val answer = StdIn.readLine()
    answer
  }

  def askPass(question: String): String = {
    print(question)
    val answer = System.console.readPassword()
    answer.mkString
  }

  @tailrec
  def samePass(): String = {
    val passwd    = askPass("Ingrese su contraseña: ")
    val passconf  = askPass("Confirme la contraseña: ")
    if(passwd == passconf)
      passwd
    else {
      println(passwd)
      println(passconf)
      println("Las contraseñas no coinciden")
      samePass()
    }
  }

  def main(args: Array[String]): Unit = {

    val dao = new UserDAOPostgres

    println("A continuación se creará la cuenta del administrador.")
    val lastName  = ask("Ingrese su apellido: ")
    val firstName = ask("Ingrese su nombre: ")
    val email     = ask("Ingrese su email: ")
    val passwd    = samePass()
        
    val user = User(None, 
                    passwd.toString(), 
                    email, 
                    Some(firstName), 
                    Some(lastName), true, true,
                    List[Role](Role.Admin(), Role.Professor()))
    
    Await.result(dao.save(user).map {
      case None => println("Error al guardar el usuario.")
      case Some(value) => println(s"Inserted with id $value")
    }, 10.seconds)
  }
}