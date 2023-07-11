package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.CategorizedBook

interface Categorizer {
    fun begin()
    fun categorize(book: BookMetadata): CategorizedBook
}