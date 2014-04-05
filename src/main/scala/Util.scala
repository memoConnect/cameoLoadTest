import com.ning.http.client.Realm.AuthScheme
import play.api.libs.json.{JsArray, Json, JsObject}
import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import dispatch._

/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 18:04
 */
object Util {

  val random = new scala.util.Random

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


  def postRequest(path: String, body: JsObject): Future[Option[JsObject]] = {
    try {
      countRequest()
//      Logger.info(path + " : " + body)
      WS.url(Config.basePath + path).withRequestTimeout(Config.requestTimeout).post(body).map {
        res =>
          res.status match {
            case x if x < 300 => parseBody(res.body)
            case _ =>
              Logger.error(path + " : " + body + " : " + res.body)
              None
          }
      }

    } catch {
      case e: Exception => Logger.error("exeption: " + e.toString)
        Future(None)
    }
  }

  def postRequest(path: String, body: JsObject, token: String): Future[Option[JsObject]] = {
    try {
      countRequest()
//      Logger.info(path + " : " + body)
      WS.url(Config.basePath + path)
        .withHeaders(("Authorization", token))
        .withRequestTimeout(Config.requestTimeout)
        .post(body).map {
        res =>
          res.status match {
            case x if x < 300 => parseBody(res.body)
            case _ =>
              Logger.error(path + " : " + body + " : " + res.body)
              None
          }
      }

    } catch {
      case e: Exception => Logger.error("exeption: e.toString")
        Future(None)
    }
  }

  def getRequest(path: String, token: String): Future[Option[JsObject]] = {
    try {
      countRequest()
//      Logger.info(path)
      WS.url(Config.basePath + path)
        .withHeaders(("Authorization", token))
        .withRequestTimeout(Config.requestTimeout)
        .get().map {
        res =>
          res.status match {
            case x if x < 300 => parseBody(res.body)
            case _ =>
              Logger.error(path + res.body)
              None
          }
      }

    } catch {
      case e: Exception => Logger.error("exeption: e.toString")
        Future(None)
    }
  }

  def getRequestWithAuth(path: String, user: String, pass: String): Future[Option[JsObject]] = {
    try {
      countRequest()

      WS.url(Config.basePath + path).withRequestTimeout(Config.requestTimeout).withAuth(user, pass, AuthScheme.BASIC).get.map {
        res =>
          res.status match {
            case x if x < 300 => parseBody(res.body)
            case _ =>
              Logger.error(path + " : " + res.body)
              None
          }
      }

    } catch {
      case e: Exception => Logger.error("exeption: e.toString")
        Future(None)
    }
  }

  def generateLoginName(): String = {

    // stolen from docker
    val left = Seq("happy", "jolly", "dreamy", "sad", "angry", "pensive", "focused", "sleepy", "grave", "distracted", "determined", "stoic", "stupefied", "sharp", "agitated", "cocky", "tender", "goofy", "furious", "desperate", "hopeful", "compassionate", "silly", "lonely", "condescending", "naughty", "kickass", "drunk", "boring", "nostalgic", "ecstatic", "insane", "cranky", "mad", "jovial", "sick", "hungry", "thirsty", "elegant", "backstabbing", "clever", "trusting", "loving", "suspicious", "berserk", "high", "romantic", "prickly", "evil")
    val right = Seq("lovelace", "franklin", "tesla", "einstein", "bohr", "davinci", "pasteur", "nobel", "curie", "darwin", "turing", "ritchie", "torvalds", "pike", "thompson", "wozniak", "galileo", "euclid", "newton", "fermat", "archimedes", "poincare", "heisenberg", "feynman", "hawking", "fermi", "pare", "mccarthy", "engelbart", "babbage", "albattani", "ptolemy", "bell", "wright", "lumiere", "morse", "mclean", "brown", "bardeen", "brattain", "shockley", "goldstine", "hoover", "hopper", "bartik", "sammet", "jones", "perlman", "wilson", "kowalevski", "hypatia", "goodall", "mayer", "elion", "blackwell", "lalande", "kirch", "ardinghelli", "colden", "almeida", "leakey", "meitner", "mestorf", "rosalind", "sinoussi", "carson", "mcclintock", "yonath")

    val name = left(random.nextInt(left.size)) + "_" + right(random.nextInt(right.size))

    name.length match {
      case x if x > 20 => generateLoginName()
      case _ => name
    }
  }

  def generateConversation(numberOfMessages: Int): Seq[String] = {
    val text = Config.conversationText.replace("\n", "").replace(". ", ".").replace(", ", ",")

    val messages = text.split(Array(',', '.')).toSeq
    val size = messages.length

    def returnMessages(left: Int): Seq[String] = {
      left match {
        case i if i > size => messages ++ returnMessages(left - size)
        case i if i == 0 => Seq()
        case i => messages.take(i)
      }
    }

    returnMessages(numberOfMessages)
  }

  def countRequest() {
    Main.requestCounter.get ! CountRequest()
  }

}
