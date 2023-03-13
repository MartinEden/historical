package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Place

data class LocationRule(val namesToMatch: Set<String>, val categorization: Categorization) : Rule {
    constructor(nameToMatch: String, categorization: Categorization)
            : this(setOf(nameToMatch), categorization)

    override fun apply(book: BookMetadata): Categorization? {
        return if (book.places.intersect(namesToMatch).any()) {
            categorization
        } else null
    }

    companion object {
        val all = listOf(
            LocationRule("England", Categorization(place = Place.Area("England", emptyList()))),
            LocationRule("Nigeria", Categorization(place = Place.Area("Nigeria", emptyList())))
        )
    }
}