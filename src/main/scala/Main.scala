import akka.actor.{PoisonPill, ActorRef, Props, ActorSystem}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  var requestRouter: Option[ActorRef] = None
  var requestCounter: Option[ActorRef] = None


  override def main(args: Array[String]) = {
    println("Starting Cameo Load Test")
    implicit val timeout: Timeout = 5.minutes

    // load config
    val loadedCfg = ConfigFactory.load()

    // start Actor system
    val system = ActorSystem("local")

    // start some actors to hande requests
    requestRouter = Some(system.actorOf(RoundRobinPool(Config.numberOfConcurrentRequests).props(Props[RequestActor]), "requestRouter"))
    
    // start counter

    // start testBatchActors
    val actorProperties = Props(classOf[TestBatchActor])

    (1 to Config.numberOfTestBatches).seq.foreach { i =>
      requestCounter = Some(system.actorOf(Props[RequestCountActor], name="requestCounter_" + i))
      val testBatchActor = system.actorOf(actorProperties, name = "batch_" + i)
      val futureRes = testBatchActor ? Start()
      Logger.info(Await.result(futureRes, 5 minutes).toString)
      requestCounter.get ! PoisonPill
    }


//
//    Thread.sleep(10000)
    system.shutdown()

    println("finished")
  }
}
