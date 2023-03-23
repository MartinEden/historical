package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Place
import eden.historical.models.countries.Country

data class LocationRule(val namesToMatch: Set<String>, val categorization: Categorization) : Rule {
    override fun apply(book: BookMetadata): Categorization? {
        return if (book.places.intersect(namesToMatch).any()) {
            categorization
        } else null
    }

    companion object {
        fun from(countries: List<Country>) = countries.map {
            LocationRule(it.names.toSet(), Categorization(place = it.asPlace()))
        }
    }
}

fun Country.asPlace() = Place.Area(defaultName, emptyList())