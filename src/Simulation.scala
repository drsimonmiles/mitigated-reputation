import java.text.SimpleDateFormat
import java.util.Date
import Chooser.{chooseFrom, randomDouble}
import Configuration.{NumberOfRounds, NumberOfSimulations, ResultsFile, Strategies}
import Results.{record, utilities}
import ResultsAccess.{loadAll, writeAll, writeAverages}
import Utilities.average

// The program root application, which simulates each strategy and outputs cumulative utility
object Simulation extends App {
  // Perform one simulation of one strategy
  def simulateStrategy (strategy: Strategy, simulation: Int) {
    val network = Network ()
    var runningUtility = 0.0
    import network._

    // Perform one round of the simulation
    def tick (round: Int) {
      // Perform a set of client-provider interactions, selecting the provider based on the current strategy
      val interactions: List[Interaction] =
      //for (client <- randomSubset (network.agents, NumberOfRequestsPerRound);
        for (client <- network.agents
             if randomDouble (0.0, 1.0) < client.requestProbability;
             service = chooseFrom (primaryCapabilities)) yield
          strategy.selectProvider (network, client, service, round).provideService (client, service, round)

      // Calculate the utility from the captured interactions, add to running total
      runningUtility += interactions.map (utility).sum
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

  // The utility gained from a single interaction
  def utility (interaction: Interaction) =
    average (interaction.ratings.values)

  // MAIN APPLICATION CODE BELOW

  // Simulate the system for each selection strategy
  for (strategy <- Strategies)
    for (simulation <- 0 until NumberOfSimulations) {
      val start = System.currentTimeMillis
      println ("Simulating " + strategy + " attempt " + simulation)
      simulateStrategy (strategy, simulation)
      println ("Done after " + (System.currentTimeMillis - start) + "ms")
    }

  val format = new SimpleDateFormat ("MM-dd-HH-mm")
  val currentAverages = "current-" + format.format (new Date) + ".csv"
  val cumulativeAverages = "cumulative-" + format.format (new Date) + ".csv"

  println ("Recording results to " + ResultsFile)
  writeAll (utilities)
  println ("Writing current averages to " + currentAverages)
  writeAverages (utilities, currentAverages)
  println ("Writing cumulative averages to " + cumulativeAverages)
  writeAverages (loadAll, cumulativeAverages)
}
