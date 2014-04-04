import akka.actor.{ActorRef, Props, ActorSystem}
import akka.routing.RoundRobinPool
import com.typesafe.config.ConfigFactory

object Main extends App {

  var requestRouter: Option[ActorRef] = None


  override def main(args: Array[String]) = {
    println("Starting Cameo Load Test")

    // load config
    val loadedCfg = ConfigFactory.load()

    // start Actor system
    val system = ActorSystem("local")

    // start some actors to hande requests
    requestRouter = Some(system.actorOf(RoundRobinPool(Config.numberOfConcurrentRequests).props(Props[RequestActor]), "requestRouter"))

    // start testBatchActors
    val actorProperties = Props(classOf[TestBatchActor])

    (1 to Config.concurrentTestBatches).foreach { i =>
      val testBatchActor = system.actorOf(actorProperties, name = "batch_" + i)
      testBatchActor ! Start()
    }


//
//    Thread.sleep(10000)
//    system.shutdown()

    println("finished")
  }
}
