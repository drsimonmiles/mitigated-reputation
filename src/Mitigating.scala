import Configuration.{FreakEventDubiousness, PoorSubproviderDubiousness, PoorOrganisationCultureDubiousness}

object Mitigating extends Strategy with FIRECore {
  val name = "Mitigating"

  // Calculate trust as in FIRE but account for mitigating circumstances
  def calculateTrustFromInteractions (interactions: Set[Interaction], round: Int): Double = {
    val weightsAndRatings = interactions.map (interaction => (calculateRelevanceWithMitigation (interaction, round), interaction.rating))
    val weightedRatings = weightsAndRatings.map (x => x._1 * x._2).sum
    val weightsSum = weightsAndRatings.map (_._1).sum
    weightedRatings / weightsSum
  }

  // Relevance includes both recency and match to mitigating circumstances patterns
  def calculateRelevanceWithMitigation (interaction: Interaction, round: Int): Double = {
    val recency = calculateRelevance (interaction, round)
    val mitigation = interaction match {
      case WithSubProvider (_, provider, _, rating, _, responseTime, _, subprovider, subservice, subresponseTime, _)
        if rating < 0.0 && responseTime < 0.0 && subresponseTime < 0.0 && provider.subproviders (subservice) != subprovider =>
        PoorSubproviderDubiousness
      case WithSubProvider (_, provider, _, rating, _, _, quality, subprovider, subservice, _, subQuality)
        if rating < 0.0 && quality < 0.0 && subQuality < 0.0 && provider.subproviders (subservice) != subprovider =>
        PoorSubproviderDubiousness
      case FreakEvent (_, _, _, rating, _, responseTime, _, timeBeforeEvent, _, _)
        if rating < 0.0 && responseTime < 0.0 && timeBeforeEvent > 0.0 =>
        FreakEventDubiousness
      case FreakEvent (_, _, _, rating, _, _, quality, _, qualityBeforeEvent, _)
        if rating < 0.0 && quality < 0.0 && qualityBeforeEvent > 0.0 =>
        FreakEventDubiousness
      case _ =>
        1.0
    }
    recency * mitigation
  }
}
