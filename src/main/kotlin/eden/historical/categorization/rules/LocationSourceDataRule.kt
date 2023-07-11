package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.withReasoning
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

    override fun apply(book: BookMetadata): Iterable<AppliedCategorization> {
        val intersection = book.places.intersect(allNames)
        return intersection.map {
            val country = lookup[it]
                ?: throw Exception("Unable to find country '$it' in lookup")
            Categorization(place = country.asPlace())
                .withReasoning(this, intersection)
        }
    }
}

fun Country.asPlace() = Place.Area(defaultName, emptyList())