/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 18:05
 */
object Logger {

  def error(msg: String) = {
    println("Error: " + msg)
  }

  def info(msg: String) = {
    println("Info: " + msg)
  }

  def stats(msg: String) = {
    println("===============================================")
    println(msg)
  }

}
