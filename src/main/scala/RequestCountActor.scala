import akka.actor.{ActorRef, Actor}
import play.api.libs.ws.WS
import scala.collection.parallel.mutable

/**
 * User: Björn Reimer
 * Date: 05.04.14
 * Time: 02:23
 */

case class CountRequest(method: String, path: String, duration: Long)

case class GetStats(replyTo: ActorRef)

case class RequestStats(count: Int, average: Int, durations: Seq[Duration])

case class Duration(method: String, path: String, total: Long, count: Int) {
  override def toString: String = {
    this.method + "  " + this.path + "  Duration: " + (total / count) + " Total: " + count
  }

  def equals(otherPath: String, otherMethod: String): Boolean = {
    otherPath.equals(this.method) && otherMethod.equals(this.path)
  }
}

class RequestCountActor extends Actor {

  val logInterval = Config.logInterval
  var requestCount = 0
  var startTime: Long = 0

  var rateTotal = 0
  var rateCount = 0


  var durations: Seq[Duration] = Seq()

  def receive = {
    case CountRequest(method, path, duration) =>

      if (requestCount == 0) {
        startTime = System.currentTimeMillis()
      }

      requestCount += 1

      // update durations
      val shortPath = path.split('/')(1)
      durations.find(_.equals(method, shortPath)) match {
        case None =>
          durations :+= Duration(method, shortPath, duration, 1)
        case Some(d) =>
          durations = durations.map {
            case d : Duration if d.equals(method, shortPath) =>
              Duration(method, shortPath, d.total + duration, d.count + 1)
            case x => x
          }
      }

      if (requestCount % logInterval == 0) {

        val duration = (System.currentTimeMillis() - startTime).toDouble / 1000
        val rate = (logInterval / duration).toInt

        rateTotal += rate
        rateCount += 1
        val average = rateTotal / rateCount

        val msg = "Request/sec: " + rate + " (avg: " + average + ")\n"
//        Logger.info("Request/sec: " + rate + " (avg: " + average + ")")
        Logger.stats(msg + durations.map(_.toString).mkString("", "\n", ""))
        durations = Seq()

        startTime = System.currentTimeMillis()
      }

    case GetStats(reply) =>
      rateCount match {
        case 0 => reply ! RequestStats(requestCount, 0, durations)
        case i => reply ! RequestStats(requestCount, rateTotal / rateCount, durations)
      }


  }

}
