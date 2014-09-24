import java.lang.Math.{log, sqrt, PI}

// Configuration of the simulation
object Configuration {
  // Number of rounds to simulate per strategy tested
  val NumberOfRounds = 200
  // Number of simulations to average over per strategy tested
  val NumberOfSimulations = 20
  // Strategies to be simulated
  val Strategies = List (Mitigating, NoStrategy, FIRE, FIREWithoutRecency)

  // Number of agents in a simulated network
  val NumberOfAgents = 200
  // Size of neighbourhood-determining spherical grid world
  val GridWidth = 500
  // The number of neighbours each agent should have on average
  val ExpectedNumberOfNeighbours = 3
  // Radius around agent within which other agents are neighbours
  val NeighbourRadius = sqrt (ExpectedNumberOfNeighbours / (NumberOfAgents * PI)) * GridWidth

  // Number of capabilities clients can request as services (primary capabilities)
  val NumberOfPrimaryCapabilities = 10
  // Number of capabilities per agent
  val NumberOfCapabilitiesPerAgent = 3

  // Minimum per-agent probability for requesting in a given round
  val MinimumRequestProbability = 0.5
  // Probability that a client will not choose the most trustworthy provider (and, if not, the probability it will not choose the next, etc.)
  val ExplorationProbability = 0.2

  // In recency scaling, the number of rounds before an interaction rating should be half that of the current round
  val RecencyScalingPeriodToHalf = 5
  // FIRE's recency scaling factor for interaction ratings (lambda)
  val RecencyScalingFactor = -RecencyScalingPeriodToHalf / log (0.5)
  // The relevancy weight based on recency below which an interaction should not be considered at all and so can be forgotten
  val IrrelevancyWeight = 0.01
  // Limit on number of interactions remembered by each agent
  val MemoryLimit = (-RecencyScalingFactor * log (IrrelevancyWeight)).round.toInt

  // Probability that a primary capability depends on a secondary capability to be performed
  val ProbabilityOfDependence = 0.3
  // Minimum number of rounds between an agent choosing new sub-providers
  val MinimumSwitchPeriod = 5
  // Maximum number of rounds between an agent choosing new sub-providers
  val MaximumSwitchPeriod = 15

  // Probability of a freak event affecting provision of a service for which no sub-service is required
  val FreakEventProbability = 0.01
  // The effect of a freak event on the timeliness of provision
  val FreakEventDelay = -1.0
  // The effect of a freak event on the quality of provision
  val FreakEventDestruction = -0.5

  // How unconvincing each mitigating circumstance is (0.0 is fully convincing, 1.0 is not at all)
  val PoorSubproviderDubiousness = 0.2
  val FreakEventDubiousness = 0.1
  val PoorOrganisationCultureDubiousness = 0.2
}
