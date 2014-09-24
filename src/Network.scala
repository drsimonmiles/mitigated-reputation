import Capability.allCapabilities
import Configuration.NumberOfAgents
import Utilities.toMap

// A set of agents, each connected to a set of neighbours
class Network private () {
  // The set of agents in the network
  val agents = List.fill (NumberOfAgents) (new Agent (this))

  // A map from each capability to the agents with that capability
  val capableOf: Map[Capability, List[Agent]] = toMap (allCapabilities) { capability =>
    agents.filter (_.capabilities.contains (capability))
  }

  // Checks that this network contains at least one agent with each capability
  def checkFeasible: Boolean =
    allCapabilities.map (capableOf (_).size > 0).reduce (_ && _)

  // Initialise all agents in the network
  def initialise () {
    agents.foreach (_.initialise ())
  }
}

object Network {
  // Creates a network of agents which has been checked to contain at least one agent with each capability
  def apply (): Network = {
    val network = new Network
    if (network.checkFeasible)
      network
    else
      apply ()
  }
}
