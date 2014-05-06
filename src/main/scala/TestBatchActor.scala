import akka.actor.{PoisonPill, ActorRef, Props, Actor}

/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:20
 *
 */

case class Start()

case class CreateConversations()

case class MakeEveryoneFriends()

case class UserCreated(login: String, iid: String)

case class TokenCreated(user: UserCreated, token: String)

case class FriendRequestSuccess(iid: String)

case class FriendsAccepted()

case class ConversationFinished()

class TestBatchActor extends Actor {

  //  implicit val timeout = Timeout(5 minutes)

  var reply: ActorRef = self
  var users: Seq[UserCreated] = Seq()
  var tokens: Seq[TokenCreated] = Seq()
  var numberOfUsers = 0
  var friends = 0
  var finishedConversations = 0
  var startTime: Long = 0
  var endTime: Long = 0

  def receive() = {

    case Start() => {
      reply = sender
      numberOfUsers = Config.numberOfUsers
      startTime = System.currentTimeMillis()
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

      // update display name
      val first: String = tokenCreated.user.login.split("_")(0)
      val second: String = tokenCreated.user.login.split("_")(1)

      def capitalize(s: String) = s(0).toUpper + s.substring(1, s.length).toLowerCase
      val displayName = capitalize(first) + " " + capitalize(second)
      Main.requestRouter.get ! UpdateIdentity(self, tokenCreated.token, displayName)
      Logger.info("display Name: " + displayName)

      // add external contacts
      Config.externalContacts.foreach {
        ec =>
          Main.requestRouter.get ! AddExternalContact(tokenCreated.token, ec.displayName, ec.email, ec.phoneNumber)
      }

      if (tokens.length == numberOfUsers) {
        self ! MakeEveryoneFriends()
      }

    case MakeEveryoneFriends() =>
      // first user sends friend request to all others
      tokens.tail.map {
        token =>
          Main.requestRouter.get ! SendFriendRequest(self, tokens.head.token, token.user.iid)
      }

    case FriendRequestSuccess(iid) =>
      // accept friend request
      tokens.find(_.user.iid.equals(iid)).map {
        token =>
          Main.requestRouter.get ! AcceptFriendRequest(self, token.token, tokens.head.user.iid)
      }

    case FriendsAccepted() =>
      friends += 1
      if (friends == numberOfUsers - 1) {
        //        Logger.info("allFriends!")
        self ! CreateConversations
      }


    case CreateConversations =>
      (1 to Config.numberOfConversations).foreach {
        i =>
          val cActor = context.actorOf(Props[ConversationActor], name = "convesation_" + i)
          cActor ! StartConversation(self, tokens, Config.numberOfMessagesPerConversation)
      }

    case ConversationFinished() =>
      finishedConversations += 1
      //Logger.info("conversation finished. Total: " + finishedConversations )

      if (finishedConversations == Config.numberOfConversations) {
        endTime = System.currentTimeMillis()
        Main.requestCounter.get ! GetStats(self)
      }

    case RequestStats(count, average, durations) =>
      val total = (endTime - startTime) / 1000
      //      val secs = total % 60
      //      val minutes = total / 60
      val msg = "Duration: " + total + " seconds, " + average + " Requests/second\n" +
        durations.map(_.toString).mkString("\n")
      Logger.info("Batch finished: " + msg)
      reply ! "ok"
      self ! PoisonPill

  }


}


