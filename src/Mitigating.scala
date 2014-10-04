import Configuration.{FreakEventDubiousness, DifferentSubproviderDubiousness, PoorOrganisationCultureDubiousness}

object Mitigating extends Strategy with FIRECore {
  val name = "Mitigating"

  def calculateRelevance (interaction: Interaction, term: Term, round: Int): Double =
    interaction match {
      case WithSubProvider (_, provider, _, _, _, subprovider, subservice, subratings)
        if provider.subproviders (subservice) != subprovider =>
        DifferentSubproviderDubiousness
      case FreakEvent (_, _, _, _, ratings, beforeEvent, _)
        if ratings (term) < 0.0 && beforeEvent (term) > 0.0 =>
        FreakEventDubiousness
      case _ =>
        1.0
    }
}
