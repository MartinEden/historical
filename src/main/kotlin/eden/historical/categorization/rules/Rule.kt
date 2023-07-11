package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.models.BookMetadata

interface Rule {
    fun apply(book: BookMetadata): AppliedCategorization?
}