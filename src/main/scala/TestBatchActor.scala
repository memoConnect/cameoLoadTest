import akka.actor.{Props, Actor}
import akka.util.Timeout
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:20
 */

case class Start()
case class CreateConversations()
case class MakeEveryoneFriends()

case class UserCreated(login: String, iid: String)
case class TokenCreated(user: UserCreated, token: String)
case class FriendRequestSuccess(cameoId: String)
case class FriendsAccepted()

class TestBatchActor extends Actor {

//  implicit val timeout = Timeout(5 minutes)

  var users: Seq[UserCreated] = Seq()
  var tokens: Seq[TokenCreated] = Seq()
  var numberOfUsers = 0
  var friends = 0

  def receive() = {

    case Start() => {
      numberOfUsers = Config.numberOfUsers
      (1 to numberOfUsers) foreach {
        i =>
          Main.requestRouter.get ! CreateUser(self)
      }
    }

    case user: UserCreated =>
      Logger.info("User created: " + user.login)
      users :+= user
      Main.requestRouter.get ! GetToken(self, user)

    case tokenCreated: TokenCreated =>
      Logger.info("token created: " + tokenCreated.token)
      tokens :+= tokenCreated

      Config.externalContacts.foreach { ec =>
        Main.requestRouter.get ! AddExternalContact(tokenCreated.token, ec.displayName, ec.email, ec.phoneNumber)
      }

      if(tokens.length == numberOfUsers) {
        self ! MakeEveryoneFriends()
      }

    case MakeEveryoneFriends() =>
      // first user sends friend request to all others
      tokens.tail.map { token =>
        Main.requestRouter.get ! SendFriendRequest(self, tokens.head.token, token.user.iid)
      }

    case FriendRequestSuccess(iid) =>
      // accept friend request
      tokens.find(_.user.iid.equals(iid)).map { token =>
        Main.requestRouter.get ! AcceptFriendRequest(self, token.token, tokens.head.user.iid)
      }

    case FriendsAccepted() =>
      friends += 1
      if ( friends == numberOfUsers - 1 ) {
        Logger.info("allFriends!")
        self ! CreateConversations
      }


    case CreateConversations =>
      ( 1 to Config.numberOfConversations).foreach { i =>
        val cActor = context.actorOf(Props[ConversationActor])
          cActor ! StartConversation(tokens, Config.numberOfMessagesPerConversation)
      }

  }


  }


