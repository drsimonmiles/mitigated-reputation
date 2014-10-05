import Chooser.{chooseFrom, ifHappens}
import Configuration.{PossibleCompetencies, ProbabilityOfBadCulture}
import Utilities.createMap

// An organisation that an agent can be working for
class Organisation (network: Network) {
  import network._

  // The organisation's competence at supporting agents in providing each of its capabilities, with regard to each term (provision feature)
  //val competence: Map[Capability, Map[Term, Double]] = createMap (primaryCapabilities) (createMap (terms) (chooseFrom (PossibleCompetencies)))
  val culture = ifHappens (ProbabilityOfBadCulture) (0.0) (1.0)
  val competence: Map[Capability, Map[Term, Double]] = createMap (primaryCapabilities) (createMap (terms) (culture))
}
