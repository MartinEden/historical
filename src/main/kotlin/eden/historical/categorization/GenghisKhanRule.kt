package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Century

object GenghisKhanRule : Rule {
    override fun apply(book: BookMetadata): Categorization? {
        return if ("khan" in book.synopsis && "Mongolia" in book.places) {
            Categorization(
                period = Century(12).period withConfidence 0.2f
            )
        } else null
    }
}