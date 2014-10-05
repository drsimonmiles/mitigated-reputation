import Configuration.{FreakEventDubiousness, DifferentSubproviderDubiousness, DifferentOrganisationCultureDubiousness, OrganisationsMatter}

// A strategy accounting for specific situations and mitigating circumstances
object Mitigating extends Strategy with FIRECore {
  val name = "Mitigating"

  def calculateRelevance (interaction: Interaction, term: Term, round: Int): Double = {
    val orgDubiousness =
      if (OrganisationsMatter && interaction.organisation != interaction.provider.organisation)
        DifferentOrganisationCultureDubiousness
      else
        1.0
    interaction match {
      case WithSubProvider (_, provider, _, organisation, _, _, subprovider, subservice, subratings)
        if provider.subproviders (subservice) != subprovider =>
        DifferentSubproviderDubiousness * orgDubiousness
      case FreakEvent (_, _, _, organisation, _, ratings, beforeEvent, _)
        if ratings (term) < 0.0 && beforeEvent (term) > 0.0 =>
        FreakEventDubiousness * orgDubiousness
      case _ =>
        orgDubiousness
    }
  }
}
