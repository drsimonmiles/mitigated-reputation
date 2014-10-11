import java.io.PrintWriter
import Configuration.{NumberOfSimulations, NumberOfRounds, Strategies}
import Utilities.{average, standardDeviation, toMap}

// Simulation results table
object Results {
  // Table of results per simulation per round
  val utilities = toMap[Strategy, Array[Array[Double]]] (Strategies) (_ => Array.fill (NumberOfSimulations, NumberOfRounds) (0.0))

  // Record a new result into the table
  def record (strategy: Strategy, simulation: Int, round: Int, result: Double) {
    utilities (strategy)(simulation)(round) = result
  }

  // Write the results out to a stream
  def write (out: PrintWriter) {
    out.print ("Round,")
    out.println (Strategies.map (s => s.name + " avg," + s.name + " std dev").mkString (","))
    for (round <- 0 until NumberOfRounds) {
      out.print (round)
      for (strategy <- Strategies) {
        val values = utilities (strategy).map (_ (round))
        val mean = average (values)
        val deviation = standardDeviation (values)
        out.print (",")
        out.print (mean)
        out.print (",")
        out.print (deviation)
      }
      out.println ()
    }
  }
}
