package eden.historical.categorization

import eden.historical.models.*
import eden.historical.models.countries.Country

class RuleBasedCategorizer(countries: List<Country>) : Categorizer {
    private val rules = sequence {
        val civilWarPeriod = Period.Range("American Civil War", 1861, 1865)

        yield(TheYearIsRule)
        yield(AnyYearRule)
        yield(DecadeRule)
        yield(
            TextRule(
                setOf(
                    SearchTerm("World War II", 1f),
                    SearchTerm("Nazi", 0.25f)
                ),
                Categorization(
                    Period.Range("World War II", 1939, 1945) withConfidence 1f,
                    Place.Area("Europe", emptyList()) withConfidence 0.25f
                )
            )
        )
        yield(
            SnippetRule(
                "Taliban",
                Categorization(period = Century(20).period withConfidence 0.25f)
            )
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
                    period = civilWarPeriod withConfidence 0.9f,
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
        yield(
            SnippetRule(
                "Roman Empire",
                Categorization(
                    period = Period.Range("Roman Empire", -27, 476) withConfidence 0.1f
                )
            )
        )
        yield(
            SnippetRule(
                "Gettysburg",
                Categorization(
                    period = civilWarPeriod withConfidence 0.2f
                )
            )
        )
        yield(
            SnippetRule(
                setOf("Jesus's life", "Life of Jesus"),
                Categorization(
                    period = Century(1).period withConfidence 0.2f
                )
            )
        )
        yield(
            TagRule(
                "Westerns",
                Categorization(
                    period = Century(19).period withConfidence 0.2f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.2f
                )
            )
        )
        yield(
            TagRule(
                "Ancient History",
                Categorization(
                    period = Period.Range("Ancient history", -5000, 476) withConfidence 0.25f
                )
            )
        )
        yieldAll(CenturyRule.all)
        yieldAll(LocationRule.from(countries))
        yield(LocationRule(setOf("Yorkshire"), Place.Area("Yorkshire", emptyList())))
        yield(SourceYearRule)
        yield(HandwrittenCategorizationRule())
        yield(GenghisKhanRule)
    }.toList()

    private val defaultCategorization = Categorization(
        Period.Unknown withConfidence 0f,
        Place.Unknown withConfidence 0f
    )

    override fun categorize(book: BookMetadata): CategorizedBook {
        val candidates = rules.mapNotNull { it.apply(book) } + defaultCategorization

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
