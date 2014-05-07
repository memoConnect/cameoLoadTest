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
