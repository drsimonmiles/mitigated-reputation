import java.lang.Math.{log, sqrt, PI}

// Configuration of the simulation
object Configuration {
  // Number of rounds to simulate per strategy tested
  val NumberOfRounds = 1000
  // Number of simulations to average over per strategy tested
  val NumberOfSimulations = 10
  // Strategies to be simulated
  val Strategies = List (FIRE, FIREWithoutRecency, Mitigating, NoStrategy)
  // Name of file to add result data to
  val ResultsFile = "100Agents1000RoundsWithOrgs.csv"

  // Number of agents in a simulated network
  val NumberOfAgents = 100
  // Size of neighbourhood-determining spherical grid world
  val GridWidth = 500
  // The number of neighbours each agent should have on average
  val ExpectedNumberOfNeighbours = 3
  // Radius around agent within which other agents are neighbours
  val NeighbourRadius = sqrt (ExpectedNumberOfNeighbours / (NumberOfAgents * PI)) * GridWidth

  // Whether organisations have any affect in the simulations
  val OrganisationsMatter = true
  // Number of organisations in a simulated network to which agents can belong
  val NumberOfOrganisations = 10
  // Probability of an organisation having a bad culture
  val ProbabilityOfBadCulture = 0.3
  // Normalised effect of good and bad organisation culture on service provision
  val CultureEffects =
    if (ProbabilityOfBadCulture < 0.5)
      (ProbabilityOfBadCulture / (1.0 - ProbabilityOfBadCulture), -1.0)
    else
      (1.0, (1.0 - ProbabilityOfBadCulture) / ProbabilityOfBadCulture)

  // Number of capabilities clients can request as services (primary capabilities)
  val NumberOfPrimaryCapabilities = 5
  // Number of capabilities per agent
  val NumberOfCapabilitiesPerAgent = 3
  
  // Number of terms (service provision features) per service
  val NumberOfTerms = 2
  // Number of possible competency values between -1.0 and 1.0 for a specific agent, capability and term
  val NumberOfCompetencies = 11
  // Possible competency values
  val PossibleCompetencies = (0 until NumberOfCompetencies).map (_.toDouble * 2.0 / (NumberOfCompetencies - 1) - 1.0)

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
  val ProbabilityOfDependence = 1.0
  // Minimum number of rounds between an agent choosing new sub-providers or organisation
  val MinimumSwitchPeriod = 5
  // Maximum number of rounds between an agent choosing new sub-providers or organisation
  val MaximumSwitchPeriod = 15

  // Probability of a freak event affecting provision of a service for which no sub-service is required
  val FreakEventProbability = 0.25

  // How unconvincing each mitigating circumstance is (1.0 is not at all, 0.0 is fully convincing)
  val DifferentSubproviderDubiousness = 0.5
  val FreakEventDubiousness = 0.1
  val DifferentOrganisationCultureDubiousness = 0.5
}
