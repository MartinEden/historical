package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Place
import eden.historical.models.countries.Country

data class LocationRule(val namesToMatch: Set<String>, val place: Place) : Rule {
    override fun apply(book: BookMetadata): Categorization? {
        return if (book.places.intersect(namesToMatch).any()) {
            Categorization(place = place)
        } else if (synopsisRegex in book.synopsis) {
            Categorization(place = place withConfidence 0.25f)
        } else null
    }

    private val synopsisRegex by lazy {
        Regex(namesToMatch.joinToString("|") { "(${asWordRegex(it) })" })
    }

    private fun asWordRegex(str: String) = "([^a-zA-Z]|^)${str.lowercase()}([^a-zA-Z]|$)"

    companion object {
        fun from(countries: List<Country>) = countries.map {
            LocationRule(it.names.toSet(), it.asPlace())
        }
    }
}

fun Country.asPlace() = Place.Area(defaultName, emptyList())