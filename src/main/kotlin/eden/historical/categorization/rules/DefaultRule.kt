package eden.historical.categorization.rules

import eden.historical.categorization.*
import eden.historical.models.BookMetadata
import eden.historical.models.Period
import eden.historical.models.Place

object DefaultRule : Rule {
    val categorization = Categorization(Period.Unknown, Place.Unknown)
        .weightedBy(0f)
        .withReasoning(this, "")

    override fun apply(book: BookMetadata) = categorization
}