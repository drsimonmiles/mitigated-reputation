import Capability.allCapabilities
import Chooser.{chooseFrom, ifHappens, randomDouble, randomInt, randomSubset}
import Configuration._
import Math.sqrt
import Utilities.toMap
import scala.annotation.tailrec

// Static methods to help construct an agent
object Agent {
  // Calculates the distance between two agents in the spherical grid world
  def distance (agent1: Agent, agent2: Agent): Double = {
    // The minimum distance along one dimension given two coordinates
    def minDistance (p1: Int, p2: Int) =
      (p1 - p2).abs.min (((p1 + GridWidth) - p2).abs).min ((p1 - (p2 + GridWidth)).abs)

    val dx = minDistance (agent1.position._1, agent2.position._1).toDouble
    val dy = minDistance (agent1.position._2, agent2.position._2).toDouble
    sqrt (dx * dx + dy * dy)
  }

  // Retrieves all direct and indirect acquaintances of a given agent, including the start agent itself
  def gatherAcquaintances (startAgent: Agent): Set[Agent] = {
    // Recursively retrieve the acquaintances of the agents in toAsk, adding to known
    @tailrec
    def gatherAcquaintances (known: Set[Agent], toAsk: Set[Agent]): Set[Agent] =
      if (toAsk.isEmpty)
        known
      else {
        val list = toAsk.toList   // To ensure head and tail provide consistent exclusive values
        gatherAcquaintances (known + list.head, list.head.neighbours.filter (n => !known.contains (n)) ++ list.tail)
      }

    gatherAcquaintances (Set (), Set (startAgent))
  }
}
import Agent._

// An agent in the network
class Agent (network: Network) {
  // The agent's (x, y) position in the neighbourhood-determining grid
  val position = (randomInt (0, GridWidth), randomInt (0, GridWidth))

  // The agent's neighbours in the network (lazily loaded to ensure all agents in network created before neighbours chosen)
  lazy val neighbours: Set[Agent] = network.agents.filter (other => other != this && distance (this, other) <= NeighbourRadius).toSet

  // All direct and indirect acquaintances of this agent, including this agent itself
  lazy val acquaintances: Set[Agent] = gatherAcquaintances (this)

  // The agent's set of capabilities
  val capabilities: List[Capability] = randomSubset (allCapabilities, NumberOfCapabilitiesPerAgent)

  // The probability that this agent will request a service in any round
  val requestProbability = randomDouble (MinimumRequestProbability, 1.0)

  // The agent's competence at performing each of its capabilities, with regard to the quality of the result
  val qualityCompetence: Map[Capability, Double] = toMap (capabilities) (_ => chooseFrom (List (-1.0, 0.0, 1.0)).get)

  // The agent's competence at performing each of its capabilities, with regard to the timeliness of the response
  val timelinessCompetence: Map[Capability, Double] = toMap (capabilities) (_ => chooseFrom (List (-1.0, 0.0, 1.0)).get)

  // The secondary capabilities for which the agent must depend on a subprovider
  val dependentFor: List[Capability] = capabilities.flatMap (_.dependsOn).filter (!capabilities.contains (_))

  // Rounds until the next re-selection of sub-providers (first set on initialisation)
  var periodToSwitch: Int = 0

  // Sub-providers currently chosen to provide capabilities the agent depends on others for (first set on initialisation)
  var subproviders: Map[Capability, Agent] = Map ()

  // History of interactions this agent has been engaged in as a client
  var provenanceStore: List[Interaction] = Nil
  //val provenanceStore = scala.collection.mutable.Map[(Agent, Capability), List[Interaction]] ()

  // Initialise the agent ready for simulation
  def initialise () {
    reselect ()
  }

  // Select new sub-providers and choose a new period before the next re-selection
  def reselect () {
    subproviders = toMap (dependentFor) (capability => chooseFrom (network.capableOf (capability)).get)
    periodToSwitch = randomInt (MinimumSwitchPeriod, MaximumSwitchPeriod + 1)
  }

  // Records the completion of a round, triggering re-selection of sub-providers if appropriate
  def endOfRound () {
    if (periodToSwitch <= 1) reselect () else periodToSwitch -= 1
  }

  // Request that this agent perform a service interacting with a client, storing and returning the provenance record documenting what happened
  def provideService (client: Agent, service: Capability, round: Int): Interaction = {
    // Create an interaction where no sub-provider is required
    def provideServiceWithoutDependence: Interaction =
      ifHappens (FreakEventProbability) (provideServiceWithFreakEvent) (provideBasicService)
    // Create an interaction where no mitigating circumstances occur
    def provideBasicService: Interaction = {
      val timeliness = timelinessCompetence (service)
      val quality = qualityCompetence (service)
      val rating = (timeliness + quality) / 2.0
      BasicProvision (client, this, service, rating, round, timeliness, quality)
    }
    // Create an interaction where a freak event affects provision
    def provideServiceWithFreakEvent: Interaction = {
      val timelinessBefore = timelinessCompetence (service)
      val qualityBefore = qualityCompetence (service)
      val timeliness = (timelinessBefore + FreakEventDelay) / 2.0
      val quality = (qualityBefore + FreakEventDestruction) / 2.0
      val rating = (timeliness + quality) / 2.0
      FreakEvent (client, this, service, rating, round, timeliness, quality, timelinessBefore, qualityBefore, "storm")
    }
    // Create an interaction where provision relies on a sub-provider
    def provideServiceWithDependence (subservice: Capability): Interaction = {
      val subprovider = subproviders (subservice)
      val subTimeliness = subprovider.timelinessCompetence (subservice)
      val subQuality = subprovider.qualityCompetence (subservice)
      val timeliness = (subTimeliness + timelinessCompetence (service)) / 2.0
      val quality = (subQuality + qualityCompetence (service)) / 2.0
      val rating = (timeliness + quality) / 2.0
      WithSubProvider (client, this, service, rating, round, timeliness, quality, subprovider, subservice, subTimeliness, subQuality)
    }

    val interaction = service.dependsOn match {
      // The service does not need a sub-service to provide
      case None => provideServiceWithoutDependence
      // The service does need a sub-service to provide, but this agent can also provide the sub-service
      case Some (subservice) if !dependentFor.contains (subservice) => provideServiceWithoutDependence
      // The service does need a sub-service to provide, and this agent must rely on a sub-provider
      case Some (subservice) if dependentFor.contains (subservice) => provideServiceWithDependence (subservice)
    }
    client.recordProvenance (interaction)
    interaction
  }

  // Record an interaction to the agent's provenance store (should be one in which this agent was the client)
  def recordProvenance (interaction: Interaction) {
    provenanceStore = (interaction :: provenanceStore).take (MemoryLimit)
  }

  // Retrieve the provenance records related to a given provider and a given service type from the agent's own store and that of its acquaintances
  def gatherProvenance (provider: Agent, service: Capability): Set[Interaction] =
    acquaintances.flatMap (_.provenanceStore.filter (interaction => interaction.provider == provider && interaction.service == service))
}
