import Chooser.getIfHappens
import Configuration.{NumberOfPrimaryCapabilities, ProbabilityOfDependence}

// A service that an agent may be capable of providing, optionally dependent on another capability
class Capability (val dependsOn: Option[Capability])

// The sets of capabilities used in the simulation
object Capability {
  // List of primary capabilities, each optionally depending on a secondary capability
  val primaryCapabilities = List.fill (NumberOfPrimaryCapabilities) {
    new Capability (getIfHappens (ProbabilityOfDependence) (new Capability (None)))
  }

  // List of secondary capabilities
  val secondaryCapabilities = primaryCapabilities.flatMap (_.dependsOn)

  // List of all capabilities
  val allCapabilities = primaryCapabilities ::: secondaryCapabilities
}
