import akka.actor.{PoisonPill, ActorRef, Props, ActorSystem}
import akka.routing.{SmallestMailboxRouter, RoundRobinPool}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
 import ExecutionContext.Implicits.global
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
    requestRouter = Some(system.actorOf(SmallestMailboxRouter(Config.numberOfConcurrentRequests).props(Props[RequestActor]), "requestRouter"))
    
    // start testBatchActors
    val actorProperties = Props(classOf[TestBatchActor])

    (1 to Config.numberOfTestBatches).seq.foreach { i =>
      requestCounter = Some(system.actorOf(Props[RequestCountActor], name="requestCounter_" + i))

      val res: Seq[Future[Any]] = (1 to Config.concurrentTestBatches).map { j =>
        val testBatchActor = system.actorOf(actorProperties, name = "batch_" + i + "_" + j)
        testBatchActor ? Start()
      }

      val msg = Await.result(Future.sequence(res), 5 minutes).mkString("\n")

      Logger.info("Finished: \n" + msg)

      requestCounter.get ! PoisonPill
    }

    requestRouter.get ! PoisonPill
//
//    Thread.sleep(10000)
//    system.shutdown()

    println("finished")
  }
}
