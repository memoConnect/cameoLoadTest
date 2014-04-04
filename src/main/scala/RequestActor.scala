import akka.actor.{ActorRef, Actor}
import akka.util.Timeout
import play.api.libs.json.{JsObject, Json}
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:40
 */

case class CreateUser(replyTo: ActorRef, email: Option[String] = None, phoneNumber: Option[String] = None)

case class GetToken(replyTo: ActorRef, user: UserCreated)

case class AddExternalContact(token: String, displayName: String, email: Option[String], phoneNumber: Option[String])

case class SendFriendRequest(replyTo: ActorRef, token: String, iid: String)

case class AcceptFriendRequest(replyTo: ActorRef, token: String, iid: String)

case class CreateConversationAndAddRecipients(replyTo: ActorRef, token: String, subject: String, recipients: Seq[String])

case class SendMessage(replyTo: ActorRef, token: String, cid: String, text: String)

class RequestActor extends Actor {

  def receive = {

    case CreateUser(reply, email, phoneNumber) =>
      val loginName = Util.generateLoginName()
      val maxTries = 25

      val futureLogin = getValidUsername(maxTries, loginName)

      futureLogin.map {
        case (login, secret) =>
          val json = Json.obj(
            "loginName" -> login,
            "password" -> Config.defaultPassword,
            "reservationSecret" -> secret
          )
          Util.postRequest("/account", json).map {
            case None => Logger.error("none2")
            case Some(js) =>
              val identity = (js \ "identities")(0).as[JsObject]
              reply ! UserCreated(login, (identity \ "id").as[String])
          }
      }

    case GetToken(reply, user) =>
      Util.getRequestWithAuth("/token", user.login, Config.defaultPassword).map {
        case None => Logger.error("none3")
        case Some(js) =>
          val token = (js \ "token").as[String]
          reply ! TokenCreated(user, token)
      }

    case AddExternalContact(token, displayName, email, phoneNumber) =>
      val json = Json.obj("displayName" -> displayName) ++
        jsonOrEmpty("email", email) ++
        jsonOrEmpty("phoneNumber", phoneNumber)
      Util.postRequest("/contact", Json.obj("identity" -> json), token)

    case SendFriendRequest(reply, token, iid) =>
      val json = Json.obj("identityId" -> iid)
      Util.postRequest("/friendRequest", json, token).map {
        case None => Logger.error("none4")
        case Some(js) => reply ! FriendRequestSuccess(iid)
      }

    case AcceptFriendRequest(reply, token, iid) =>
      val json = Json.obj("identityId" -> iid, "answerType" -> "accept")
      Util.postRequest("/friendRequest/answer", json, token).map {
        case None => Logger.error("none5")
        case Some(js) => reply ! FriendsAccepted()
      }


    case CreateConversationAndAddRecipients(reply, token, subject, recipients) =>
      val json = Json.obj("subject" -> subject)
      Util.postRequest("/conversation", json, token).map {
        case None => Logger.error("none6")
        case Some(js) =>
          val cid = (js \ "id").as[String]

          val json2 = Json.obj("recipients" -> recipients)
          Util.postRequest("/conversation/" + cid + "/recipient", json2, token).map {
            case None => Logger.error("none6")
            case Some(js) => reply ! ConversationCreated(cid)
          }
      }

    case SendMessage(reply, token, cid, text) =>
      val json = Json.obj("body" -> text)
      Util.postRequest("/conversation/" + cid + "/message", json, token).map {
        case None => Logger.error("none7")
        case Some(js) => reply ! MessageSend()
      }

  }

  def getValidUsername(tries: Int, loginName: String): Future[(String, String)] = {
    if (tries > 0) {
      Util.postRequest("/account/check", Json.obj("loginName" -> loginName)).flatMap {
        case None => Logger.error("none8"); Future(("", ""))
        case Some(js) =>
          (js \ "reservationSecret").asOpt[String] match {
            case Some(s) => Future((loginName, s))
            case None =>
              (js \ "alternative").asOpt[String] match {
                case None => Logger.error("none10"); Future(("", ""))
                case Some(alt) =>
                  // use alternative
                  Util.postRequest("/account/check", Json.obj("loginName" -> alt)).flatMap {
                    case None => Logger.error("none11"); Future(("", ""))
                    case Some(js) =>
                      (js \ "reservationSecret").asOpt[String] match {
                        case Some(s) => Future((alt, s))
                        case None =>  getValidUsername(tries - 1, loginName) // try again
                      }
                  }
              }
          }
      }
    } else {
      Future(("", ""))
    }
  }

  def jsonOrEmpty(key: String, value: Option[String]): JsObject = {
    value match {
      case None => Json.obj()
      case Some(s) => Json.obj(key -> s)
    }
  }
}