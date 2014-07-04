import akka.actor.{ActorRef, Actor}
import akka.util.Timeout
import com.ning.http.client.Realm.AuthScheme
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.libs.ws.{Response, WS}
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:40
 */

case class SetCounter(counter: ActorRef)
case class CreateUser(replyTo: ActorRef, email: Option[String] = None, phoneNumber: Option[String] = None)
case class GetToken(replyTo: Option[ActorRef], user: UserCreated)
case class AddExternalContact(token: String, displayName: String, email: Option[String], phoneNumber: Option[String])
case class SendFriendRequest(replyTo: ActorRef, token: String, iid: String)
case class AcceptFriendRequest(replyTo: ActorRef, token: String, iid: String)
case class CreateConversationAndAddRecipients(replyTo: ActorRef, token: String, subject: String, recipients: Seq[String])
case class SendMessage(replyTo: ActorRef, token: String, cid: String, text: String)
case class GetConversation(replyTo: ActorRef, token: String, cid: String)
case class UpdateIdentity(replyTo: ActorRef, token: String, displayName: String)
case class GetRequest(replyTo: ActorRef,path: String, token: String)
case class DeleteTestUser(testUserId: String)


class RequestActor extends Actor {

  var requestCounter: ActorRef = null

  def receive = {

    case SetCounter(counter) => requestCounter = counter

    case CreateUser(reply, email, phoneNumber) =>
      val loginName = Util.generateLoginName()
      val maxTries = 25

      val futureLogin = getValidUsername(maxTries, loginName)

      futureLogin.map {
        case (login, secret) =>
//          Logger.info("Login: " + login)
          val json = Json.obj(
            "loginName" -> login,
            "password" -> Config.defaultPassword,
            "reservationSecret" -> secret
          )
          postRequest("/account", json).map {
            case None => Logger.error("none2")
            case Some(js) =>
              val identity = (js \ "identities")(0).as[JsObject]
              reply ! UserCreated(login, (identity \ "id").as[String])
          }
      }

    case GetToken(reply, user) =>
      getRequestWithAuth("/token", user.login, Config.defaultPassword).map {
        case None => Logger.error("none3")
        case Some(js) =>
          val token = (js \ "token").as[String]
          reply match {
            case None =>
            case Some(ref) => ref ! TokenCreated(user, token)
          }
      }

    case UpdateIdentity(reply, token, displayName) =>
      val json = Json.obj("displayName" -> displayName)
      putRequest("/identity", json, token)

    case GetRequest(reply, path, token) =>
      getRequest(path, token).map {
        case None => Logger.error("None: GetRequest")
        case Some(js) => reply ! GetSuccess()
      }


    case AddExternalContact(token, displayName, email, phoneNumber) =>
      val json = Json.obj("displayName" -> displayName) ++
        jsonOrEmpty("email", email) ++
        jsonOrEmpty("phoneNumber", phoneNumber)
      postRequest("/contact", Json.obj("identity" -> json), token)

    case SendFriendRequest(reply, token, iid) =>
      val json = Json.obj("identityId" -> iid)
      postRequest("/friendRequest", json, token).map {
        case None => Logger.error("none4")
        case Some(js) => reply ! FriendRequestSuccess(iid)
      }

    case AcceptFriendRequest(reply, token, iid) =>
      val json = Json.obj("identityId" -> iid, "answerType" -> "accept")
      postRequest("/friendRequest/answer", json, token).map {
        case None => Logger.error("none5")
        case Some(js) => reply ! FriendsAccepted()
      }


    case CreateConversationAndAddRecipients(reply, token, subject, recipients) =>

      val json = Json.obj("subject" -> subject)
      postRequest("/conversation", json, token).map {
        case None => Logger.error("none6")
        case Some(js) =>
          val cid = (js \ "id").as[String]

          val json2 = Json.obj("recipients" -> recipients)
          postRequest("/conversation/" + cid + "/recipient", json2, token).map {
            case None => Logger.error("none6")
            case Some(js) => reply ! ConversationCreated(cid)
          }
      }

    case SendMessage(reply, token, cid, text) =>
      val json = Json.obj("plain" -> Json.obj("text" -> text))
      postRequest("/conversation/" + cid + "/message", json, token).map {
        case None => Logger.error("none7")
        case Some(js) => reply ! MessageSend()
      }

    case GetConversation(reply, token, cid) =>
      getRequest("/conversation/" + cid + "?limit=25", token)

    case DeleteTestUser(id) =>
      Logger.info("Deleting testUser: " + id)
      deleteRequest("/testUser/" + id)
  }

  def parseBody(body: String): Option[JsObject] = {
    val json = Json.parse(body)
    (json \ "data").asOpt[JsObject] match {
      case Some(s) => Some(s)
      case None =>
        (json \ "data").asOpt[JsArray] match {
          case Some(a) => Some(Json.obj("array" -> a))
          case None => Some(Json.obj())
        }
    }
  }

  /*
    Helper Methods
   */

  def postRequest(path: String, body: JsObject): Future[Option[JsObject]] = {
    def req =  WS
      .url(Config.basePath + path)
      .withRequestTimeout(Config.requestTimeout)
      .post(body)

    executeRequest("POST", path, req)

  }

  def postRequest(path: String, body: JsObject, token: String): Future[Option[JsObject]] = {
    def req = WS
      .url(Config.basePath + path)
      .withHeaders(("Authorization", token))
      .withRequestTimeout(Config.requestTimeout)
      .post(body)

    executeRequest("POST", path, req)
  }

  def getRequest(path: String, token: String): Future[Option[JsObject]] = {

    def req = WS
      .url(Config.basePath + path)
      .withHeaders(("Authorization", token))
      .withRequestTimeout(Config.requestTimeout)
      .get()

    executeRequest("GET", path, req)
  }

  def getRequestWithAuth(path: String, user: String, pass: String): Future[Option[JsObject]] = {

    def req = WS
      .url(Config.basePath + path)
      .withRequestTimeout(Config.requestTimeout)
      .withAuth(user, pass, AuthScheme.BASIC)
      .get()

    executeRequest("GET", path, req)
  }

  def deleteRequest(path: String): Future[Option[JsObject]] = {

    def req = WS
      .url(Config.basePath + path)
      .withRequestTimeout(Config.requestTimeout)
      .get()

    executeRequest("DELETE", path, req)
  }

  def putRequest(path: String, body: JsObject, token: String): Future[Option[JsObject]] = {
    def req = WS
      .url(Config.basePath + path)
      .withHeaders(("Authorization", token))
      .withRequestTimeout(Config.requestTimeout)
      .put(body)

    executeRequest("PUT", path, req)
  }

  def executeRequest(method: String, path: String, request: => Future[Response]): Future[Option[JsObject]] = {

    val start = System.currentTimeMillis()
    try {
      request.map {
        res =>
          res.status match {
            case x if x < 300 =>
              val duration = System.currentTimeMillis() - start
              requestCounter ! CountRequest(method, path, duration)
              parseBody(res.body)
            case _ =>
              Logger.error(method + " " + path + " : " + res.body)
              None
          }
      }
    } catch {
      case e: Exception => Logger.error("Could not execute request: " + e.toString)
        Future(None)
    }
  }

  def getValidUsername(tries: Int, loginName: String): Future[(String, String)] = {
    if (tries > 0) {

      // make sure the loginname is shorter than 20 chars
      val validLoginName = loginName match {
        case l if l.length > 20 => l.substring(l.length - 20)
        case _ => loginName
      }

      postRequest("/account/check", Json.obj("loginName" -> validLoginName)).flatMap {
        case None => Logger.error("none8"); Future(("", ""))
        case Some(js) =>
          (js \ "reservationSecret").asOpt[String] match {
            case Some(s) => Future((validLoginName, s))
            case None =>
              (js \ "alternative").asOpt[String] match {
                case None => Logger.error("none10"); Future(("", ""))
                case Some(alt) =>
                  // use alternative
                  postRequest("/account/check", Json.obj("loginName" -> alt)).flatMap {
                    case None => Logger.error("none11, alternative: " + alt); Future(("", ""))
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