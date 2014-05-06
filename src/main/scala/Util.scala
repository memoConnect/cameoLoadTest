import com.ning.http.client.Realm.AuthScheme
import play.api.libs.json.{JsArray, Json, JsObject}
import play.api.libs.ws.{Response, WS}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

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
    def req =  WS
      .url(Config.basePath + path)
      .withRequestTimeout(Config.requestTimeout)
      .post(body)

      executeRequest(path, req)

    }

  def postRequest(path: String, body: JsObject, token: String): Future[Option[JsObject]] = {
    def req = WS
      .url(Config.basePath + path)
      .withHeaders(("Authorization", token))
      .withRequestTimeout(Config.requestTimeout)
      .post(body)

    executeRequest(path, req)
  }

  def getRequest(path: String, token: String): Future[Option[JsObject]] = {

    def req = WS
      .url(Config.basePath + path)
      .withHeaders(("Authorization", token))
      .withRequestTimeout(Config.requestTimeout)
      .get()

    executeRequest(path, req)
  }

  def getRequestWithAuth(path: String, user: String, pass: String): Future[Option[JsObject]] = {

    def req = WS
      .url(Config.basePath + path)
      .withRequestTimeout(Config.requestTimeout)
      .withAuth(user, pass, AuthScheme.BASIC)
      .get()

    executeRequest(path, req)
  }

  def putRequest(path: String, body: JsObject, token: String): Future[Option[JsObject]] = {
    def req = WS
      .url(Config.basePath + path)
      .withHeaders(("Authorization", token))
      .withRequestTimeout(Config.requestTimeout)
      .put(body)

    executeRequest(path, req)
  }

  def executeRequest(desc: String, request: => Future[Response]): Future[Option[JsObject]] = {

    val start = System.currentTimeMillis()
    try {
      request.map {
        res =>
          res.status match {
            case x if x < 300 =>
              val duration = System.currentTimeMillis() - start
              Main.requestCounter.get ! CountRequest(desc ,duration)
              parseBody(res.body)
            case _ =>
              Logger.error(desc + " : " + res.body)
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

  def generateConversation(numberOfMessages: Int): (String, Seq[String]) = {

    val length: Int = (random.nextGaussian() * (numberOfMessages / 4) + numberOfMessages).toInt

    val posLength = length match {
      case x if x > 0 => x
      case 0 => 1
      case x => x * -1
    }

    val textNum = random.nextInt(Config.conversationTexts.length)
    val subject = Config.conversationTexts(textNum)._1

    val text = Config.conversationTexts(textNum)._2.replace("\n", "").replace(". ", ".").replace(", ", ",")

    val messages = text.split(Array(',', '.')).toSeq
    val size = messages.length

    def returnMessages(left: Int): Seq[String] = {
      left match {
        case i if i > size => messages ++ returnMessages(left - size)
        case i if i == 0 => Seq()
        case i => messages.take(i)
      }
    }

    (subject,returnMessages(posLength))
  }

  def countRequest() {
  }

}
