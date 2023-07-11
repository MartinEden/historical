package eden.historical.categorization.rules

import eden.historical.categorization.Categorization
import eden.historical.models.BookMetadata

interface Rule {
    fun apply(book: BookMetadata): Categorization?
}