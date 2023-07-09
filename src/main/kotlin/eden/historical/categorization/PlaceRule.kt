package eden.historical.categorization

import eden.historical.models.BookMetadata

class PlaceRule(private val matchesPlaces: Set<String>, private val categorization: Categorization) : Rule {
    constructor(matchTag: String, categorization: Categorization)
            : this(setOf(matchTag), categorization)

    override fun apply(book: BookMetadata): Categorization? {
        return if (book.places.intersect(matchesPlaces).any()) {
            categorization
        } else null
    }
}