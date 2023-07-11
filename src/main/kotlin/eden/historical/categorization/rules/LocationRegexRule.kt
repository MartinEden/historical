package eden.historical.categorization.rules

import eden.historical.categorization.*
import eden.historical.models.BookMetadata
import eden.historical.models.countries.Country

class LocationRegexRule(val countries: List<Country>): RegexRule(buildRegex(countries)) {
    private val lookup: Map<String, Country> = sequence {
        for (country in countries) {
            for (name in country.names) {
                yield(name.lowercase() to country)
            }
        }
    }.toMap()

    override fun handleMatch(match: MatchResult, fullText: String): Categorization {
        val country = lookup[match.value]
            ?: throw Exception("Unable to find country '${match.value}' in lookup")

        var categorization = Categorization(place = country.asPlace() withConfidence 0.25f)

        val redHerringRegexes = listOfNotNull(
            if (country.defaultName == "New York") Regex("new york times") else null
        )
        if (redHerringRegexes.any { it in fullText }) {
            // TODO: Would be good to be able to output reasoning here
            categorization = categorization.weightedBy(0.001f)
        }
        return categorization
    }

    companion object {
        fun buildRegex(countries: List<Country>): Regex {
            return multiWordOptionRegex(countries.flatMap { it.names })
        }

        private fun multiWordOptionRegex(snippets: Iterable<String>): Regex {
            val snippetRegexes = snippets.joinToString("|") { it.lowercase() }
            // This tries to find the sequence of characters as a whole word (rather than finding 'Oman' in 'Romance').
            // On either side of the word we look for either a non-alphabetic character (so a space or a punctuation mark)
            // or for the beginning/end of the string.
            return Regex("(?<![a-zA-Z])($snippetRegexes)(?![a-zA-Z])")
        }
    }
}