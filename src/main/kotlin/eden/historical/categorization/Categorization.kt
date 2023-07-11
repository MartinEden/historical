package eden.historical.categorization

import eden.historical.models.Period
import eden.historical.models.Place

data class Categorization(
    val period: InfoWithConfidence<Period>? = null,
    val place: InfoWithConfidence<Place>? = null
) {
    constructor(period: Period? = null, place: Place? = null)
            : this(
        period?.let { InfoWithConfidence(period) },
        place?.let { InfoWithConfidence(place) }
    )

    val totalConfidence = listOfNotNull(0f, period?.confidence, place?.confidence).sum()

    fun weightedBy(confidenceMultiplier: Float) = Categorization(
        period = this.period?.weightedBy(confidenceMultiplier),
        place = this.place?.weightedBy(confidenceMultiplier)
    )
}

data class InfoWithConfidence<T>(
    val info: T,
    val confidence: Float = 1f
) {
    fun weightedBy(confidenceMultiplier: Float) = copy(confidence = confidence * confidenceMultiplier)
}

infix fun <T> T.withConfidence(confidence: Float) = InfoWithConfidence(this, confidence)

fun <T> Iterable<InfoWithConfidence<T>>.highestConfidenceInfo(): List<T> {
    val groups = this
        .groupBy({ it.confidence }, { it.info })
        .toList()
        .sortedByDescending { (confidence, _) -> confidence }
    val (_, items) = groups.first()
    return items
}