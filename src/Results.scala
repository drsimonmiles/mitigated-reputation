import java.io.PrintWriter
import Configuration.{NumberOfSimulations, NumberOfRounds, Strategies}
import Utilities.toMap

// Simulation results table
object Results {
  // Table of results per simulation per round
  val utilities = toMap (Strategies) (_ => Array.fill (NumberOfSimulations, NumberOfRounds) (0.0))

  // Calculate the average score across simulations for a given round using a given strategy
  def average (strategy: Strategy, round: Int) =
    utilities (strategy).map (_ (round)).sum / NumberOfSimulations

  // Record a new result into the table
  def record (strategy: Strategy, simulation: Int, round: Int, result: Double) {
    utilities (strategy)(simulation)(round) = result
  }

  // Write the results out to a stream
  def write (out: PrintWriter) {
    out.print ("Round,")
    out.println (Strategies.map (_.name).mkString (","))
    for (round <- 0 until NumberOfRounds) {
      out.print (round)
      out.print (",")
      out.println (Strategies.map (strategy => average (strategy, round)).mkString (","))
    }
  }
}
