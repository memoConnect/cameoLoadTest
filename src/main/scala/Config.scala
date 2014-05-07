/**
 * User: Björn Reimer
 * Date: 03.04.14
 * Time: 17:57
 */

case class ExternalContact(displayName: String, email: Option[String], phoneNumber: Option[String])

object Config {

//  var basePath = "https://dev.cameo.io/api/v1"
  var basePath = "https://stage.cameo.io/api/v1"

  // bundle of tasks that will be repeated
  var numberOfTestBatches = 1

  // number of parallel threads
  var concurrentTestBatches = 1        //1  Time: 54s, 5150Request: 60s(one);32s(two);34(four)

  var defaultPassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"

  var requestTimeout = 40000


  var numberOfConcurrentRequests = 50  //30,50

  // Batch config
  // number of users that will be created
  var numberOfUsers = 5

  var numberOfConversations = 50      //40,40

  var numberOfMessagesPerConversation = 200


  var externalContacts: Seq[ExternalContact] = Seq(
    ExternalContact("Björn", Some("loadTest@bjrm.de"), None),
    ExternalContact("Micha", None, Some("+491633625340")),
    ExternalContact("DevTeam", Some("dev@cameo.io"), None)
  )

  var conversationTexts: Seq[(String, String)] = Seq(
    ("Einstein", """
Wenn Ihr den Rundfunk höret, so denkt auch daran, wie die Menschen in den Besitz dieses wunderbaren Werkzeuges der Mitteilung gekommen sind. Der Urquell aller technischen Errungenschaften ist die göttliche Neugier und der Spieltrieb des bastelnden und grübelnden Forschers und nicht minder die konstruktive Phantasie des technischen Erfinders.
Denkt an Oersted, der zuerst die magnetische Wirkung elektrischer Ströme bemerkte, an Reis, der diese Wirkung zuerst benutzte, um auf elektromagnetischem Wege Schall zu erzeugen, an Bell, der unter Benutzung empfindlicher Kontakte mit seinem Mikrophon zuerst Schallschwingungen in variable elektrische Ströme verwandelte. Denkt auch an Maxwell, der die Existenz elektrischer Wellen auf mathematischem Wege aufzeigte, an Hertz, der sie zuerst mit Hilfe des Funkens erzeugte und nachwies. Gedenket besonders auch Liebens, der in der elektrischen Ventilröhre ein unvergleichliches Spürorgan für elektrische Schwingungen erdachte, das sich zugleich als ideal einfaches Instrument zur Erzeugung elektrischer Schwingungen herausstellte. Gedenket dankbar des Heeres namenloser Techniker, welche die Instrumente des Radio-Verkehres so vereinfachten und der Massenfabrikation anpassten, dass sie jedermann zugänglich geworden sind.
Sollen sich auch alle schämen, die gedankenlos sich der Wunder der Wissenschaft und Technik bedienen und nicht mehr davon geistig erfasst haben als die Kuh von der Botanik der Pflanzen, die sie mit Wohlbehagen frisst.
Denket auch daran, dass die Techniker es sind, die erst wahre Demokratie möglich machen. Denn sie erleichtern nicht nur des Menschen Tagewerk, sondern machen auch die Werke der feinsten Denker und Künstler, deren Genuss noch vor kurzem ein Privileg bevorzugter Klassen war, der Gesamtheit zugänglich und erwecken so die Völker aus schläfriger Stumpfheit.
Was speziell den Rundfunk anlangt, so hat er eine einzigartige Funktion zu erfüllen im Sinne der Völkerversöhnung. Bis auf unsere Tage lernten die Völker einander fast ausschließlich durch den verzerrenden Spiegel der eigenen Tagespresse kennen. Der Rundfunk zeigt sie einander in lebendigster Form und in der Hauptsache von der liebenswürdigen Seite. Er wird so dazu beitragen, das Gefühl gegenseitiger Fremdheit auszutilgen, das so leicht in Misstrauen und Feindseligkeit umschlägt.
Betrachtet in dieser Gesinnung die Ergebnisse des Schaffens, welche diese Ausstellung den staunenden Sinnen des Besuchers darbietet.
    """),
    ("Schneewitchen",
      """
        |Es war einmal mitten im Winter, und die Schneeflocken fielen wie Federn vom Himmel herab, da saß eine Königin an einem Fenster, das einen Rahmen von schwarzem Ebenholz hatte, und nähte. Und wie sie so nähte und nach dem Schnee aufblickte, stach sie sich mit der Nadel in den Finger, und es fielen drei Tropfen Blut in den Schnee. Und weil das Rote im weißen Schnee so schön aussah, dachte sie bei sich 'hätt ich ein Kind so weiß wie Schnee, so rot wie Blut, und so schwarz wie das Holz an dem Rahmen.' Bald darauf bekam sie ein Töchterlein, das war so weiß wie Schnee, so rot wie Blut, und so schwarzhaarig wie Ebenholz, und ward darum das Schneewittchen (Schneeweißchen) genannt. Und wie das Kind geboren war, starb die Königin.
        |
        |Über ein Jahr nahm sich der König eine andere Gemahlin. Es war eine schöne Frau, aber sie war stolz und übermütig, und konnte nicht leiden, daß sie an Schönheit von jemand sollte übertroffen werden. Sie hatte einen wunderbaren Spiegel, wenn sie vor den trat und sich darin beschaute, sprach sie
        |
        |'Spieglein, Spieglein an der Wand,
        |wer ist die Schönste im ganzen Land?'
        |
        |so antwortete der Spiegel
        |
        |'Frau Königin, Ihr seid die Schönste im Land.'
        |
        |Da war sie zufrieden, denn sie wußte, daß der Spiegel die Wahrheit sagte.
        |
        |Schneewittchen aber wuchs heran und wurde immer schöner, und als es sieben Jahre alt war, war es so schön wie der klare Tag, und schöner als die Königin selbst. Als diese einmal ihren Spiegel fragte
        |
        |'Spieglein, Spieglein an der Wand,
        |wer ist die Schönste im ganzen Land?'
        |
        |so antwortete er
        |
        |'Frau Königin, Ihr seid die Schönste hier,
        |aber Schneewittchen ist tausendmal schöner als Ihr.'
        |
        |Da erschrak die Königin und ward gelb und grün vor Neid. Von Stund an, wenn sie Schneewittchen erblickte, kehrte sich ihr das Herz im Leibe herum, so haßte sie das Mädchen. Und der Neid und Hochmut wuchsen wie ein Unkraut in ihrem Herzen immer höher, daß sie Tag und Nacht keine Ruhe mehr hatte. Da rief sie einen Jäger und sprach 'bring das Kind hinaus in den Wald, ich wills nicht mehr vor meinen Augen sehen. Du sollst es töten und mir Lunge und Leber zum Wahrzeichen mitbringen.' Der Jäger gehorchte und führte es hinaus, und als er den Hirschfänger gezogen hatte und Schneewittchens unschuldiges Herz durchbohren wollte, fing es an zu weinen und sprach 'ach, lieber Jäger, laß mir mein Leben; ich will in den wilden Wald laufen und nimmermehr wieder heim kommen.' Und weil es so schön war, hatte der Jäger Mitleid und sprach 'so lauf hin, du armes Kind.' 'Die wilden Tiere werden dich bald gefressen haben,' dachte er, und doch wars ihm, als wär ein Stein von seinem Herzen gewälzt, weil er es nicht zu töten brauchte. Und als gerade ein junger Frischling dahergesprungen kam, stach er ihn ab, nahm Lunge und Leber heraus, und brachte sie als Wahrzeichen der Königin mit. Der Koch mußte sie in Salz kochen, und das boshafte Weib aß sie auf und meinte, sie hätte Schneewittchens Lunge und Leber gegessen.
        |
        |Nun war das arme Kind in dem großen Wald mutterseelig allein, und ward ihm so angst, daß es alle Blätter an den Bäumen ansah und nicht wußte, wie es sich helfen sollte. Da fing es an zu laufen und lief über die spitzen Steine und durch die Dornen, und die wilden Tiere sprangen an ihm vorbei, aber sie taten ihm nichts. Es lief, solange nur die Füße noch fort konnten, bis es bald Abend werden wollte, da sah es ein kleines Häuschen und ging hinein, sich zu ruhen. In dem Häuschen war alles klein, aber so zierlich und reinlich, daß es nicht zu sagen ist. Da stand ein weißgedecktes Tischlein mit sieben kleinen Tellern, jedes Tellerlein mit seinem Löffelein, ferner sieben Messerlein und Gäblein, und sieben Becherlein. An der Wand waren sieben Bettlein nebeneinander aufgestellt und schneeweiße Laken darüber gedeckt. Schneewittchen, weil es so hungrig und durstig war, aß von jedem Tellerlein ein wenig Gemüs und Brot, und trank aus jedem Becherlein einen Tropfen Wein; denn es wollte nicht einem allein alles wegnehmen. Hernach, weil es so müde war, legte es sich in ein Bettchen, aber keins paßte; das eine war zu lang, das andere zu kurz, bis endlich das siebente recht war: und darin blieb es liegen, befahl sich Gott und schlief ein.
        |
        |Als es ganz dunkel geworden war, kamen die Herren von dem Häuslein, das waren die sieben Zwerge, die in den Bergen nach Erz hackten und gruben. Sie zündeten ihre sieben Lichtlein an, und wie es nun hell im Häuslein ward, sahen sie, daß jemand darin gewesen war, denn es stand nicht alles so in der Ordnung, wie sie es verlassen hatten. Der erste sprach 'wer hat auf meinem Stühlchen gesessen?' Der zweite 'wer hat von meinem Tellerchen gegessen?' Der dritte 'wer hat von meinem Brötchen genommen?' Der vierte 'wer hat von meinem Gemüschen gegessen?' Der fünfte 'wer hat mit meinem Gäbelchen gestochen?' Der sechste 'wer hat mit meinem Messerchen geschnitten?' Der siebente 'wer hat aus meinem Becherlein getrunken?' Dann sah sich der erste um und sah, daß auf seinem Bett eine kleine Delle war, da sprach er 'wer hat in mein Bettchen getreten?' Die andern kamen gelaufen und riefen 'in meinem hat auch jemand gelegen.' Der siebente aber, als er in sein Bett sah, erblickte Schneewittchen, das lag darin und schlief. Nun rief er die andern, die kamen herbeigelaufen, und schrien vor Verwunderung, holten ihre sieben Lichtlein und beleuchteten Schneewittchen. 'Ei, du mein Gott! ei, du mein Gott!' riefen sie, 'was ist das Kind so schön!' und hatten so große Freude, daß sie es nicht aufweckten, sondern im Bettlein fortschlafen ließen. Der siebente Zwerg aber schlief bei seinen Gesellen, bei jedem eine Stunde, da war die Nacht herum.
      """.stripMargin),
    ("Lorem ipsum",
      """
        |Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
        |
        |Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.
        |
        |Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.
        |
        |Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.
        |
        |Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis.
        |
        |At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, At accusam aliquyam diam diam dolore dolores duo eirmod eos erat, et nonumy sed tempor et et invidunt justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat.
        |
        |Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus.
        |
        |Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
        |
        |Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.
        |
        |Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.
        |
        |Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo
      """.stripMargin)
  )
}
