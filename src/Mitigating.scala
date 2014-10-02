import Configuration.{FreakEventDubiousness, PoorSubproviderDubiousness, PoorOrganisationCultureDubiousness}

object Mitigating extends Strategy with FIRECore {
  val name = "Mitigating"

  def calculateRelevance (interaction: Interaction, term: Term, round: Int): Double =
    interaction match {
      case WithSubProvider (_, provider, _, _, _, subprovider, subservice, subratings)
        if subratings (term) < 0.0 && provider.subproviders (subservice) != subprovider =>
        PoorSubproviderDubiousness
      case FreakEvent (_, _, _, _, ratings, beforeEvent, _)
        if ratings (term) < 0.0 && beforeEvent (term) > 0.0 =>
        FreakEventDubiousness
      case _ =>
        1.0
    }
}
