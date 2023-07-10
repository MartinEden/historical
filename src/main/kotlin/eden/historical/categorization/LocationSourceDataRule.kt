package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Place
import eden.historical.models.countries.Country

data class LocationSourceDataRule(val namesToMatch: Set<String>, val place: Place): Rule {
    override fun apply(book: BookMetadata): Categorization? {
        return if (book.places.intersect(namesToMatch).any()) {
            Categorization(place = place)
        } else null
    }

    companion object {
        fun from(countries: List<Country>) = countries.map {
            LocationSourceDataRule(it.names.toSet(), it.asPlace())
        }
    }
}

fun Country.asPlace() = Place.Area(defaultName, emptyList())