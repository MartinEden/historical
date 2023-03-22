package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.CategorizedBook
import eden.historical.models.Period
import eden.historical.models.Place
import eden.historical.models.countries.Country

class RuleBasedCategorizer(countries: List<Country>) : Categorizer {
    private val rules = sequence {
        yield(TheYearIsRule)
        yield(
            TagRule(
                "World War II",
                Categorization(
                    Period("World War II", 1939, 1945),
                    Place.Area("Europe", emptyList())
                )
            )
        )
        yield(
            TagRule(
                "Scotland",
                Categorization(place = Place.Area("Scotland", emptyList()))
            )
        )
        yield(
            TagRule(
                "Tudor Period",
                Categorization(period = Period("Tudor", 1485, 1603))
            )
        )
        yield(
            TagRule(
                "American Revolutionary War",
                Categorization(
                    period = Period("American Revolutionary War", 1775, 1783),
                    place =  Place.Area(countries.single { it.iso3 == "USA" }.name, emptyList())
                )
            )
        )
        yieldAll(CenturyRule.all)
        yieldAll(LocationRule.from(countries))
        yield(HandwrittenCategorizationRule())
    }

    private val defaultCategorization = Categorization(Period.default, Place.Unknown)

    override fun categorize(book: BookMetadata): CategorizedBook {
        val candidates = rules.mapNotNull { it.apply(book) } + defaultCategorization
        // TODO: distinguish by confidence
        // TODO: distinguish by specificity
        val period = candidates.firstNotNullOf { it.period }
        val place = candidates.firstNotNullOf { it.place }
        return CategorizedBook(book.book, period, place)
    }
}
