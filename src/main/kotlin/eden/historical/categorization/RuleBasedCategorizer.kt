package eden.historical.categorization

import eden.historical.models.*
import eden.historical.models.countries.Country

class RuleBasedCategorizer(countries: List<Country>) : Categorizer {
    private val rules = sequence {
        yield(TheYearIsRule)
        yield(AnyYearRule)
        yield(DecadeRule)
        yield(
            TagRule(
                "World War II",
                Categorization(
                    Period.Range("World War II", 1939, 1945),
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
                Categorization(period = Period.Range("Tudor", 1485, 1603))
            )
        )
        yield(
            TagRule(
                "Regency",
                Categorization(
                    period = Period.Range("Regency", 1795, 1837),
                    // TODO: Needs low confidence
                    place = countries.single { it.defaultName == "England" }.asPlace()
                )
            )
        )
        yield(
            TagRule(
                "Prehistoric",
                Categorization(period = Period.Range("Prehistoric", -20000, -5000))
            )
        )
        yield(
            TagRule(
                "Greek Mythology",
                Categorization(period = Period.Range("Greek mythology", -2000, -1000))
            )
        )
        yield(
            TagRule(
                "American Revolutionary War",
                Categorization(
                    period = Period.Range("American Revolutionary War", 1775, 1783),
                    place =  countries.single { it.iso3 == "USA" }.asPlace()
                )
            )
        )
        yieldAll(CenturyRule.all)
        yieldAll(LocationRule.from(countries))
        yield(SourceYearRule)
        yield(HandwrittenCategorizationRule())
    }

    private val defaultCategorization = Categorization(Period.Unknown, Place.Unknown)

    override fun categorize(book: BookMetadata): CategorizedBook {
        val candidates = rules.mapNotNull { it.apply(book) } + defaultCategorization
        // TODO: distinguish period & place by confidence

        // TODO: take smallest intersection? e.g. 15th century / tudor
        val period = candidates
            .map { it.period }
            .filterNotNull()
            .sortedWith(Period.Specificity())
            .firstOrNull() ?: Period.Unknown

        // TODO: distinguish places by specificity
        val place = candidates.firstNotNullOf { it.place }
        return CategorizedBook(book.book, period, place)
    }
}
