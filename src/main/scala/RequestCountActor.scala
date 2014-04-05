import akka.actor.{ActorRef, Actor}

/**
 * User: Björn Reimer
 * Date: 05.04.14
 * Time: 02:23
 */

case class CountRequest()

case class GetStats(replyTo: ActorRef)

case class RequestStats(count: Int, average: Int)

class RequestCountActor extends Actor {

  val logInterval = 100
  var count = 0
  var startTime: Long = 0

  var rateTotal = 0
  var rateCount = 0

  def receive = {
    case CountRequest() =>

      if (count == 0) {
        startTime = System.currentTimeMillis()
      }

      count += 1

      if (count % logInterval == 0) {

        val duration = (System.currentTimeMillis() - startTime).toDouble / 1000
        val rate = (logInterval / duration).toInt

        rateTotal += rate
        rateCount += 1
        val average = rateTotal / rateCount

        Logger.info("Request/sec: " + rate + " (avg: " + average + ")")

        startTime = System.currentTimeMillis()
      }

    case GetStats(reply) =>
      reply ! RequestStats(count, rateTotal / rateCount)

  }

}
