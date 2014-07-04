import akka.actor.{PoisonPill, ActorRef, Props, Actor}

/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:20
 *
 */

case class Start(repetitions: Int, requestRouter: ActorRef)

case class CreateConversations()

case class MakeEveryoneFriends()

case class UserCreated(login: String, iid: String)

case class TokenCreated(user: UserCreated, token: String)

case class FriendRequestSuccess(iid: String)

case class FriendsAccepted()

case class ConversationFinished()

case class BatchFinished()

class TestBatchActor extends Actor {

  val numberOfUsers = Config.numberOfUsers
  
  var users: Seq[UserCreated] = Seq()
  var tokens: Seq[TokenCreated] = Seq()
  var friends = 0
  var finishedConversations = 0
  var startTime: Long = 0
  var endTime: Long = 0
  var requestRouter : ActorRef = null
  var repetitionCount = 0
  var repetitionsMax = 0

  def receive() = {

    case Start(repetitions, newRequestRouter) =>
      startTime = System.currentTimeMillis()
      requestRouter = newRequestRouter
      repetitionsMax = repetitions
      repetitionCount += 1
      users = Seq()
      tokens =Seq()
      friends = 0
      finishedConversations = 0

      (1 to numberOfUsers) foreach {
        i => requestRouter ! CreateUser(self)
      }

    case user: UserCreated =>
//      Logger.info("User created: " + user.login)
      users :+= user
      requestRouter ! GetToken(Some(self), user)

      // additional token requests
      (2 to Config.tokenPerUser) foreach { i =>
        requestRouter ! GetToken(None, user)
      }

    case tokenCreated: TokenCreated =>

      tokens :+= tokenCreated

      // update display name
      val first: String = tokenCreated.user.login.split("_")(0)
      val second: String = tokenCreated.user.login.split("_")(1)

      def capitalize(s: String) = s(0).toUpper + s.substring(1, s.length).toLowerCase
      val displayName = capitalize(first) + " " + capitalize(second)
      requestRouter ! UpdateIdentity(self, tokenCreated.token, displayName)

      // add external contacts
      Config.externalContacts.foreach {
        ec =>
          requestRouter ! AddExternalContact(tokenCreated.token, ec.displayName, ec.email, ec.phoneNumber)
      }

//      Logger.info("token created. Tokens: " + tokens + " #"+ numberOfUsers)

      if (tokens.length == numberOfUsers) {
//        Logger.info("sending message to make everyone friends")
        self ! MakeEveryoneFriends()
      }

      // some  get requests for more load
      val getIdentityActor = context.actorOf(Props[GetActor])
      getIdentityActor ! StartGetting("/identity", tokenCreated.token, Config.getIdentityPerUser, requestRouter)
      val getContactsActor = context.actorOf(Props[GetActor])
      getContactsActor ! StartGetting("/contacts", tokenCreated.token, Config.getContactsPerUser, requestRouter)
      val getConversationsActor = context.actorOf(Props[GetActor])
      getConversationsActor ! StartGetting("/conversations", tokenCreated.token, Config.getConversationsPerUser, requestRouter)

    case MakeEveryoneFriends() =>
//      Logger.info("making everyone frieds!")
      // first user sends friend request to all others
      tokens.tail.map {
        token =>
          requestRouter ! SendFriendRequest(self, tokens.head.token, token.user.iid)
      }

    case FriendRequestSuccess(iid) =>
//      Logger.info("friend request successfull")
      // accept friend request
      tokens.find(_.user.iid.equals(iid)).map {
        token =>
          requestRouter ! AcceptFriendRequest(self, token.token, tokens.head.user.iid)
      }

    case FriendsAccepted() =>
      friends += 1
      if (friends == numberOfUsers - 1) {
//                Logger.info("allFriends!")
        self ! CreateConversations
      }

    case CreateConversations =>
      (1 to Config.numberOfConversations).foreach {
        i =>
          val cActor = context.actorOf(Props[ConversationActor])
          cActor ! StartConversation(self, requestRouter,tokens, Config.numberOfMessagesPerConversation)
      }

    case ConversationFinished() =>
      finishedConversations += 1
//      Logger.info("conversation finished. Total: " + finishedConversations )

      if (finishedConversations == Config.numberOfConversations) {
        endTime = System.currentTimeMillis()
        self ! BatchFinished()
      }

    case BatchFinished() =>
      repetitionsMax match {
        case 0 =>
          // repeat forever
          self ! Start(0, requestRouter)
        case i if i < repetitionsMax =>
          self ! Start(0, requestRouter)
          Logger.info("Starting testBatch repetition: " + repetitionCount)
        case _ =>
          // we are finished, end this actor
          Logger.info("TestBatch finished")
          if(Config.deleteCreatedUsers) {
              Util.loginNames.map {
                name =>
                  val id = name.split('_')(1)
                  requestRouter ! DeleteTestUser(id)
              }
            }
          self ! PoisonPill
          }




//    case RequestStats(count, average, durations) =>
//      val total = (endTime - startTime) / 1000
//      //      val secs = total % 60
//      //      val minutes = total / 60
//      val msg = "Duration: " + total + " seconds, " + average + " Requests/second\n" +
//        durations.map(_.toString).mkString("\n")
//      Logger.info("Batch finished: " + msg)
//      reply ! "ok"
//      self ! PoisonPill

  }


}


