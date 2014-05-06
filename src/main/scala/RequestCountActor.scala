import akka.actor.{ActorRef, Actor}
import play.api.libs.ws.WS

/**
 * User: Björn Reimer
 * Date: 05.04.14
 * Time: 02:23
 */

case class CountRequest(path: String, duration: Long)

case class GetStats(replyTo: ActorRef)

case class RequestStats(count: Int, average: Int, durations: Seq[Duration])

case class Duration(path: String, total: Long, count: Int) {
  override def toString: String = {
    this.path + " : " + total / count
  }
}

class RequestCountActor extends Actor {

  val logInterval = 400
  var count = 0
  var startTime: Long = 0

  var rateTotal = 0
  var rateCount = 0


  var durations: Seq[Duration] = Seq()

  def receive = {
    case CountRequest(path, duration) =>


      if (count == 0) {
        startTime = System.currentTimeMillis()
      }

      count += 1

      // update durations
      val shortPath = path.split('/')(1)
      durations.find(_.path.equals(shortPath)) match {
        case None =>
          durations :+= Duration(shortPath, duration, 1)
        case Some(d) =>
          durations = durations.map {
            case Duration(p, oldDuration, oldCount) if p.equals(shortPath) =>
              Duration(shortPath, oldDuration + duration, oldCount + 1)
            case x => x
          }
      }

      if (count % logInterval == 0) {

        val duration = (System.currentTimeMillis() - startTime).toDouble / 1000
        val rate = (logInterval / duration).toInt

        rateTotal += rate
        rateCount += 1
        val average = rateTotal / rateCount

        Logger.info("Request/sec: " + rate + " (avg: " + average + ")")
        Logger.info(durations.map(_.toString).mkString("", "\n", ""))
        durations = Seq()

        startTime = System.currentTimeMillis()
      }

    case GetStats(reply) =>
      rateCount match{
        case 0 => reply ! RequestStats(count, 0, durations)
        case i => reply ! RequestStats(count, rateTotal / rateCount, durations)
      }


  }

}
