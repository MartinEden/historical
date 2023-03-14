package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Place
import eden.historical.models.countries.Country

data class LocationRule(val namesToMatch: Set<String>, val categorization: Categorization) : Rule {
    constructor(nameToMatch: String, categorization: Categorization)
            : this(setOf(nameToMatch), categorization)

    override fun apply(book: BookMetadata): Categorization? {
        return if (book.places.intersect(namesToMatch).any()) {
            categorization
        } else null
    }

    companion object {
        fun from(countries: List<Country>) = countries.map {
            LocationRule(it.name, Categorization(place = Place.Area(it.name, emptyList())))
        }
    }
}