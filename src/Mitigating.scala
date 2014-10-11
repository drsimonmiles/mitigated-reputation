import Configuration.{FreakEventDubiousness, DifferentSubproviderDubiousness, DifferentOrganisationCultureDubiousness, OrganisationsMatter}

// A strategy accounting for specific situations and mitigating circumstances
abstract class MitigatingCore extends Strategy with FIRECore {
  def calculateMitigation (interaction: Interaction, term: Term): Double = {
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

object Mitigating extends MitigatingCore {
  val name = "Mitigating"

  def calculateRelevance (interaction: Interaction, term: Term, round: Int): Double =
    calculateMitigation (interaction, term)
}

object MitigatingWithRecency extends MitigatingCore {
  val name = "MitigatingWithRecency"

  def calculateRelevance (interaction: Interaction, term: Term, round: Int): Double =
    calculateRecency (interaction, round) * calculateMitigation (interaction, term)
}
