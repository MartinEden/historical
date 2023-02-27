package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.CategorizedBook
import eden.historical.models.Period
import eden.historical.models.Place

class HardcodedCategorizer : Categorizer {
    private val rules = listOf(
        TheYearIsRule,
        TagRule(
            "World War II",
            Categorization(
                Period("World War II", 1939, 1945),
                Place.Area("Europe", emptyList())
            )
        ),
        TagRule(
            "Scotland",
            Categorization(place = Place.Area("Scotland", emptyList()))
        ),
        TagRule("Tudor Period", Categorization(period = Period("Tudor", 1485, 1603)))
    ) + CenturyRule.all

    private val defaultCategorization = Categorization(Period.default, Place.default)

    override fun categorize(book: BookMetadata): CategorizedBook {
        val candidates = rules.mapNotNull { it.apply(book) } + defaultCategorization
        val period = candidates.firstNotNullOf { it.period }
        val place = candidates.firstNotNullOf { it.place }
        return CategorizedBook(book.book, period, place)
    }
}
