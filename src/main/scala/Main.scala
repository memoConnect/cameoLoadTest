import akka.actor.{ PoisonPill, ActorRef, Props, ActorSystem }
import akka.routing.{ Broadcast, SmallestMailboxRouter, RoundRobinPool }
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
object Main extends App {

  override def main(args: Array[String]) = {
    println("Starting Cameo Load Test")
    implicit val timeout: Timeout = 5.minutes

    // load config
    val loadedCfg = ConfigFactory.load()

    // start Actor system
    val system = ActorSystem("local")

    // use route to manage request actors
    val requestRouter = system.actorOf(SmallestMailboxRouter(Config.numberOfConcurrentRequests).props(Props[RequestActor]), "requestRouter")

    // start request counter and broadcast it to all requestActors
    val requestCounter = system.actorOf(Props[RequestCountActor], name = "requestCounter")
    requestRouter ! Broadcast(SetCounter(requestCounter))

    // start testBatchActors
    val actorProperties = Props(classOf[TestBatchActor])
    (1 to Config.concurrentTestBatches).foreach {
      i =>
        // sleep according to rampup time
        val sleepTime = (Config.rampUpTime / Config.concurrentTestBatches) * 1000 * (i -1)
        Thread.sleep(sleepTime)

        Logger.info("Starting Test Batch Number " + i)

        val testBatchActor = system.actorOf(actorProperties, name = "testBatch_" + i)
        testBatchActor ! Start(Config.testBatchRepetitions, requestRouter)
    }
  }

  Logger.info("initialisation finished.")
}

