/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:57
 */

case class ExternalContact(displayName: String, email: Option[String], phoneNumber: Option[String])

object Config {

  var basePath = "https://dev.cameo.io/api/v1"

  var numberOfTestBatches = 1

  var concurrentTestBatches = 1        //1  Time: 54s, 5150Request: 60s(one);32s(two);34(four)

  var defaultPassword = "password"

  var requestTimeout = 40000

  var numberOfConcurrentRequests = 50  //30,50

  var numberOfUsers = 5

  var numberOfConversations = 100      //40,40

  var numberOfMessagesPerConversation = 200

  var externalContacts: Seq[ExternalContact] = Seq(
    ExternalContact("Björn", Some("loadTest@bjrm.de"), None),
    ExternalContact("Micha", None, Some("+491633625340")),
    ExternalContact("DevTeam", Some("dev@cameo.io"), None)
  )

  var conversationText =
    """
Wenn Ihr den Rundfunk höret, so denkt auch daran, wie die Menschen in den Besitz dieses wunderbaren Werkzeuges der Mitteilung gekommen sind. Der Urquell aller technischen Errungenschaften ist die göttliche Neugier und der Spieltrieb des bastelnden und grübelnden Forschers und nicht minder die konstruktive Phantasie des technischen Erfinders.
Denkt an Oersted, der zuerst die magnetische Wirkung elektrischer Ströme bemerkte, an Reis, der diese Wirkung zuerst benutzte, um auf elektromagnetischem Wege Schall zu erzeugen, an Bell, der unter Benutzung empfindlicher Kontakte mit seinem Mikrophon zuerst Schallschwingungen in variable elektrische Ströme verwandelte. Denkt auch an Maxwell, der die Existenz elektrischer Wellen auf mathematischem Wege aufzeigte, an Hertz, der sie zuerst mit Hilfe des Funkens erzeugte und nachwies. Gedenket besonders auch Liebens, der in der elektrischen Ventilröhre ein unvergleichliches Spürorgan für elektrische Schwingungen erdachte, das sich zugleich als ideal einfaches Instrument zur Erzeugung elektrischer Schwingungen herausstellte. Gedenket dankbar des Heeres namenloser Techniker, welche die Instrumente des Radio-Verkehres so vereinfachten und der Massenfabrikation anpassten, dass sie jedermann zugänglich geworden sind.
Sollen sich auch alle schämen, die gedankenlos sich der Wunder der Wissenschaft und Technik bedienen und nicht mehr davon geistig erfasst haben als die Kuh von der Botanik der Pflanzen, die sie mit Wohlbehagen frisst.
Denket auch daran, dass die Techniker es sind, die erst wahre Demokratie möglich machen. Denn sie erleichtern nicht nur des Menschen Tagewerk, sondern machen auch die Werke der feinsten Denker und Künstler, deren Genuss noch vor kurzem ein Privileg bevorzugter Klassen war, der Gesamtheit zugänglich und erwecken so die Völker aus schläfriger Stumpfheit.
Was speziell den Rundfunk anlangt, so hat er eine einzigartige Funktion zu erfüllen im Sinne der Völkerversöhnung. Bis auf unsere Tage lernten die Völker einander fast ausschließlich durch den verzerrenden Spiegel der eigenen Tagespresse kennen. Der Rundfunk zeigt sie einander in lebendigster Form und in der Hauptsache von der liebenswürdigen Seite. Er wird so dazu beitragen, das Gefühl gegenseitiger Fremdheit auszutilgen, das so leicht in Misstrauen und Feindseligkeit umschlägt.
Betrachtet in dieser Gesinnung die Ergebnisse des Schaffens, welche diese Ausstellung den staunenden Sinnen des Besuchers darbietet.
    """
}
