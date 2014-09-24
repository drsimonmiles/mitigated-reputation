import Chooser.ifHappens
import Configuration.{ExplorationProbability, RecencyScalingFactor}

// The core FIRE strategy but leaving unspecified how to calculate trust from gathered interactions
trait FIRECore extends Strategy {
  // Select the provider mostly using FIRE's model
  def selectProvider (network: Network, client: Agent, service: Capability, round: Int): Option[Agent] = {
    // Choose first agent in list, except if exploration probability met try a later agent recursively
    def selectMostTrustworthy (orderedProviders: List[Agent]): Option[Agent] = orderedProviders match {
      case Nil => None
      case mostTrusted :: rest => ifHappens (ExplorationProbability) (selectMostTrustworthy (rest)) (Some (mostTrusted))
    }

    val orderedProviders = network.capableOf (service).sortBy (calculateTrust (client, _, service, round)).reverse
    selectMostTrustworthy (orderedProviders)
  }

  // Calculates the trust in a provider for a service: T_K(a, b, c)
  def calculateTrust (client: Agent, provider: Agent, service: Capability, round: Int): Double = {
    val interactions = client.gatherProvenance (provider, service)
    calculateTrustFromInteractions (interactions, round)
  }

  // Calculate trust from a given set of interactions
  def calculateTrustFromInteractions (interactions: Set[Interaction], round: Int): Double

  // Calculates the relevance of an interaction: omega_K(r_i)
  def calculateRelevance (interaction: Interaction, round: Int): Double =
    Math.pow (Math.E, -((round - interaction.round) / RecencyScalingFactor))
}

// FIRE strategy implementation using recency to weight ratings
object FIRE extends Strategy with FIRECore {
  val name = "FIRE"

  // Calculate trust using recency as in FIRE paper
  def calculateTrustFromInteractions (interactions: Set[Interaction], round: Int): Double = {
    val weightsAndRatings = interactions.map (interaction => (calculateRelevance (interaction, round), interaction.rating))
    val weightedRatings = weightsAndRatings.map (x => x._1 * x._2).sum
    val weightsSum = weightsAndRatings.map (_._1).sum
    weightedRatings / weightsSum
  }
}

// Variation on FIRE where recency is not accounted for
object FIREWithoutRecency extends Strategy with FIRECore {
  val name = "FIREWithoutRecency"

  // Calculate trust without caring about recency
  def calculateTrustFromInteractions (interactions: Set[Interaction], round: Int): Double =
    interactions.map (_.rating).sum / interactions.size
}
