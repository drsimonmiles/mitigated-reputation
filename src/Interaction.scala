// A (summarised, OOP version of a) provenance record concerning an interaction, specialised by different scenarios
sealed abstract class Interaction {
  val client: Agent
  val provider: Agent
  val service: Capability
  val rating: Double
  val round: Int
}

// Scenario where client requests service from provider who does not depend on any other agent
case class BasicProvision (client: Agent, provider: Agent, service: Capability, rating: Double, round: Int,
                           responseTime: Double, quality: Double) extends Interaction

// Scenario where client requests service from provider who then uses a sub-provider
case class WithSubProvider (client: Agent, provider: Agent, service: Capability, rating: Double, round: Int,
                            responseTime: Double, quality: Double,
                            subprovider: Agent, subservice: Capability, subresponseTime: Double, subQuality: Double) extends Interaction

// Scenario where client requests service from a provider, but provision is affected by a freak event
case class FreakEvent (client: Agent, provider: Agent, service: Capability, rating: Double, round: Int,
                        responseTime: Double, quality: Double,
                        timeBeforeEvent: Double, qualityBeforeEvent: Double, event: String) extends Interaction
