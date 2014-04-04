import akka.actor.Actor

/**
 * User: Björn Reimer
 * Date: 04.04.14
 * Time: 13:11
 */

case class StartConversation(userTokens: Seq[TokenCreated], numberOrMessages: Int)
case class NextMessage(tokenIndex: Int)

case class ConversationCreated(cid: String)
case class MessageSend()

class ConversationActor extends Actor {

  var messages: Seq[String] = Seq()
  var userTokens: Seq[TokenCreated] = Seq()
  var cid = ""
  var currentUser = 0

  def receive = {
    case StartConversation(tokens, number) =>
      userTokens = tokens
      messages = Util.generateConversation(number)

      Main.requestRouter.get ! CreateConversationAndAddRecipients(self, userTokens.head.token, "subject", userTokens.tail.map(_.user.iid))

    case ConversationCreated(newCid) =>
      Logger.info("conversation created")
      cid = newCid
      Main.requestRouter.get ! SendMessage(self, userTokens(currentUser).token,cid, messages.head)

    case MessageSend() =>
      currentUser match {
        case x if x  >= userTokens.length - 1 => currentUser = 0
        case _ =>  currentUser += 1
      }

      messages = messages.tail

      messages.length match {
        case 0 => Logger.info("conversation finished")
        case _ => Main.requestRouter.get ! SendMessage(self, userTokens(currentUser).token,cid, messages.head)
      }






  }


}
