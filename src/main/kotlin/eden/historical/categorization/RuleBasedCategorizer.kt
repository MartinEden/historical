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
                    Period.Range("World War II", 1939, 1945) withConfidence 1f,
                    Place.Area("Europe", emptyList()) withConfidence 0.25f
                )
            )
        )
        yield(
            SnippetRule(
                matchingSnippets = setOf(
                    "World War II",
                    "Nazi",
                ),
                categorization = Categorization(
                    Period.Range("World War II", 1939, 1945) withConfidence 0.25f,
                    Place.Area("Europe", emptyList()) withConfidence 0.25f
                )
            )
        )
        yield(
            SnippetRule("Taliban", Categorization(period = Century(20).period withConfidence 0.25f))
        )
        yield(
            SnippetRule(
                "Great Depression",
                Categorization(period = Period.Range("Great Depresion", 1929, 1939))
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
                "Medieval",
                Categorization(period = Period.Range("Medieval", 500, 1500) withConfidence 0.25f)
            )
        )
        yield(
            TagRule(
                "Regency",
                Categorization(
                    period = Period.Range("Regency", 1795, 1837) withConfidence 1f,
                    place = countries.single { it.defaultName == "England" }.asPlace() withConfidence 0.1f
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
                Categorization(
                    period = Period.Range("Greek mythology", -2000, -1000) withConfidence 0.1f,
                    place = Place.Area("Mediterranean", emptyList()) withConfidence 0.1f,
                )
            )
        )
        yield(
            TagRule(
                "Arthurian",
                Categorization(
                    period = Period.Range("Arthurian mythology", 500, 1000) withConfidence 0.25f,
                    place = countries.single { it.iso3 == "GBR" }.asPlace() withConfidence 0.5f
                )
            )
        )
        yield(
            TagRule(
                setOf(
                    "American Revolutionary War",
                    "American Revolution"
                ),
                Categorization(
                    period = Period.Range("American Revolutionary War", 1775, 1783) withConfidence 0.9f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.75f
                )
            )
        )
        yield(
            TagRule(
                "Civil War",
                Categorization(
                    period = Period.Range("American Civil War", 1861, 1865) withConfidence 0.9f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.25f
                )
            )
        )
        yield(
            PlaceRule(
                "Roman Empire",
                Categorization(
                    period = Period.Range("Roman Empire", -27, 476)
                )
            )
        )
        yieldAll(CenturyRule.all)
        yieldAll(LocationRule.from(countries))
        yield(LocationRule(setOf("Yorkshire"), Place.Area("Yorkshire", emptyList())))
        yield(SourceYearRule)
        yield(HandwrittenCategorizationRule())
    }.toList()

    private val defaultCategorization = Categorization(
        Period.Unknown withConfidence 0f,
        Place.Unknown withConfidence 0f
    )

    override fun categorize(book: BookMetadata): CategorizedBook {
        val candidates = rules.toList().mapNotNull { it.apply(book) } + defaultCategorization

        // TODO: take smallest intersection? e.g. 15th century / tudor
        val period = candidates
            .mapNotNull { it.period }
            .highestConfidenceInfo()
            .sortedWith(Period.Specificity())
            .firstOrNull() ?: Period.Unknown

        // TODO: distinguish places by specificity
        // TODO: merge places when there are multiple conflicting places of equal weight. e.g. multiple states -> USA
        val place = candidates
            .mapNotNull { it.place }
            .highestConfidenceInfo()
            .first()
        return CategorizedBook(book.book, period, place)
    }
}
