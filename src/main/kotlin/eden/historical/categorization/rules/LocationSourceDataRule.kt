package eden.historical.categorization.rules

import eden.historical.categorization.Categorization
import eden.historical.models.BookMetadata
import eden.historical.models.Place
import eden.historical.models.countries.Country

class LocationSourceDataRule(val countries: List<Country>): Rule {
    private val lookup: Map<String, Country> = sequence {
        for (country in countries) {
            for (name in country.names) {
                yield(name to country)
            }
        }
    }.toMap()
    private val allNames = lookup.keys

    override fun apply(book: BookMetadata): Categorization? {
        val intersection = book.places.intersect(allNames)
        // TODO: Return all matches that result in unique countries?
        return intersection.firstOrNull()?.let {
            val country = lookup[it]
                ?: throw Exception("Unable to find country '$it' in lookup")
            Categorization(place = country.asPlace())
        }
    }
}

fun Country.asPlace() = Place.Area(defaultName, emptyList())