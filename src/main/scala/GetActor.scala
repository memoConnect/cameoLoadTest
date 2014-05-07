import akka.actor.{PoisonPill, ActorRef, Actor}

/**
 * User: Björn Reimer
 * Date: 07.05.14
 * Time: 15:19
 */

case class GetSuccess()
case class StartGetting(path: String, token: String, repetitions: Int, requestRouter: ActorRef)

class GetActor extends Actor {
  var total = 0
  var count = 0
  var path = ""
  var token = ""
  var requestRouter : ActorRef = null

  def receive() = {

    case StartGetting(newPath, newToken, repetitions, newRequestRouter) =>
      total = repetitions
      requestRouter = newRequestRouter
      path = newPath
      token = newToken
      requestRouter ! GetRequest(self, path, token)

    case GetSuccess() =>
      count += 1
      count match {
        case i if i < total =>
          requestRouter ! GetRequest(self, path, token)
        case _ =>  self ! PoisonPill
      }

  }

}
