import akka.actor.{ActorRef, Actor}
import scala.util.Random

/**
 * User: Björn Reimer
 * Date: 04.04.14
 * Time: 13:11
 */

case class StartConversation(replyTo: ActorRef, requestRouter: ActorRef, userTokens: Seq[TokenCreated], numberOrMessages: Int)
case class NextMessage(tokenIndex: Int)

case class ConversationCreated(cid: String)
case class MessageSend()

class ConversationActor extends Actor {

  var messages: Seq[String] = Seq()
  var subject: String = ""
  var userTokens: Seq[TokenCreated] = Seq()
  var cid = ""
  var currentUser = 0
  var reply : ActorRef = self
  var requestRouter: ActorRef = null

  def receive = {
    case StartConversation(replyTo, newRequestRouter, tokens, number) =>

      userTokens = tokens
      requestRouter = newRequestRouter
      val generated = Util.generateConversation(number)
      subject = generated._1
      messages = generated._2
      reply = replyTo

      requestRouter ! CreateConversationAndAddRecipients(self, userTokens.head.token, subject, userTokens.tail.map(_.user.iid))

    case ConversationCreated(newCid) =>
      cid = newCid
      requestRouter ! SendMessage(self, userTokens(currentUser).token,cid, messages.head)

    case MessageSend() =>
      currentUser match {
        case x if x  >= userTokens.length - 1 => currentUser = 0
        case _ =>  currentUser += 1
      }

      messages = messages.tail

//      Logger.info("Messages left: " + messages.length)

      messages.length match {
        case 0 =>
          reply ! ConversationFinished()
        case _ => requestRouter ! SendMessage(self, userTokens(currentUser).token,cid, messages.head)
      }

      (1 to Config.getConversationPerMessage) foreach { i =>
         requestRouter ! GetConversation(self, Random.shuffle(userTokens).head.token, cid)
      }

  }


}
