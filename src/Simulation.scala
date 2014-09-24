import java.io.{File, FileWriter, BufferedWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date
import Capability.primaryCapabilities
import Chooser.{chooseFrom, randomDouble}
import Configuration.{NumberOfRounds, NumberOfSimulations, Strategies}
import Results.{record, write}

// The program root application, which simulates each strategy and outputs cumulative utility
object Simulation extends App {
  // Perform one simulation of one strategy
  def simulateStrategy (strategy: Strategy, simulation: Int) {
    val network = Network ()
    var runningUtility = 0.0

    // Perform one round of the simulation
    def tick (round: Int) {
      // Perform a set of client-provider interactions, selecting the provider based on the current strategy
      val interactions: List[Option[Interaction]] =
        //for (client <- randomSubset (network.agents, NumberOfRequestsPerRound);
        for (client <- network.agents
             if randomDouble (0.0, 1.0) < client.requestProbability;
             service = chooseFrom (primaryCapabilities)
             if service != None) yield
          strategy.selectProvider (network, client, service.get, round).map (_.provideService (client, service.get, round))

      // Calculate the utility from the captured interactions, add to running total
      runningUtility += interactions.flatten.map (_.rating).sum
      record (strategy, simulation, round, runningUtility)

      // Notify agents of the end of the round
      for (agent <- network.agents)
        agent.endOfRound ()
    }

    // Perform all simulation rounds
    network.initialise ()
    for (round <- 0 until NumberOfRounds) {
      tick (round)
      print (".")
    }
    println ()
  }

  // Simulate the system for each selection strategy
  for (strategy <- Strategies)
    for (simulation <- 0 until NumberOfSimulations) {
      val start = System.currentTimeMillis
      println ("Simulating " + strategy + " attempt " + simulation)
      simulateStrategy (strategy, simulation)
      println ("Done after " + (System.currentTimeMillis - start) + "ms")
    }

  val format = new SimpleDateFormat ("MM-dd-HH-mm")
  val file = new File ("results-" + format.format (new Date)  + ".csv")
  println ("Recording to " + file.getAbsolutePath)
  val out = new PrintWriter (new BufferedWriter (new FileWriter (file)))
  write (out)
  out.close ()
}
