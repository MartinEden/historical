package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.withConfidence
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata
import eden.historical.models.Century

object GenghisKhanRule : Rule {
    override fun apply(book: BookMetadata): AppliedCategorization? {
        return if ("khan" in book.synopsis && "Mongolia" in book.places) {
            Categorization(
                period = Century(12).period withConfidence 0.2f
            ).withReasoning(this, mapOf(
                "synopsis" to book.synopsis,
                "places" to book.places
            ))
        } else null
    }
}