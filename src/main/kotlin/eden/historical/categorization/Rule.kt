package eden.historical.categorization

import eden.historical.models.BookMetadata

interface Rule {
    fun apply(book: BookMetadata): Categorization?
}