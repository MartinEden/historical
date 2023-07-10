package eden.historical.categorization

import eden.historical.models.*
import eden.historical.models.countries.Country

class RuleBasedCategorizer(countries: List<Country>) : Categorizer {
    private val rules = sequence {
        val civilWarPeriod = Period.Range("American Civil War", 1861, 1865)

        yield(SourceYearRule)
        yield(TheYearIsRule)
        yield(AnyYearRule)
        yield(DecadeRule)
        yieldAll(CenturyRule.all)

        yieldAll(LocationSourceDataRule.from(countries))
        yield(LocationRegexRule(countries))
        yield(LocationRegexRule(listOf(Country(listOf("Yorkshire"), null, emptyList()))))

        yield(
            TextRule(
                setOf(
                    SearchTerm.Plain("World War II", 1f),
                    SearchTerm.Plain("WWII", 0.9f),
                    SearchTerm.Plain("Nazi", 0.25f)
                ),
                Categorization(
                    Period.Range("World War II", 1939, 1945) withConfidence 1f,
                    Place.Area("Europe", emptyList()) withConfidence 0.5f
                )
            )
        )
        yield(
            TextRule(
                "Taliban",
                Categorization(period = Century(20).period withConfidence 0.25f)
            )
        )
        yield(
            TextRule(
                "Great Depression",
                Categorization(period = Period.Range("Great Depresion", 1929, 1939))
            )
        )
        yield(
            TextRule(
                "Scotland",
                Categorization(place = Place.Area("Scotland", emptyList()))
            )
        )
        yield(
            TextRule(
                "Tudor Period",
                Categorization(period = Period.Range("Tudor", 1485, 1603))
            )
        )
        yield(
            TextRule(
                "Medieval",
                Categorization(period = Period.Range("Medieval", 500, 1500) withConfidence 0.25f)
            )
        )
        yield(
            TextRule(
                "Regency",
                Categorization(
                    period = Period.Range("Regency", 1795, 1837) withConfidence 1f,
                    place = countries.single { it.defaultName == "England" }.asPlace() withConfidence 0.1f
                )
            )
        )
        yield(
            TextRule(
                "Prehistoric",
                Categorization(period = Period.Range("Prehistoric", -20000, -5000))
            )
        )
        yield(
            TextRule(
                "Greek Mythology",
                Categorization(
                    period = Period.Range("Greek mythology", -2000, -1000) withConfidence 0.1f,
                    place = Place.Area("Mediterranean", emptyList()) withConfidence 0.1f,
                )
            )
        )
        yield(
            TextRule(
                "Arthurian",
                Categorization(
                    period = Period.Range("Arthurian mythology", 500, 1000) withConfidence 0.25f,
                    place = countries.single { it.iso3 == "GBR" }.asPlace() withConfidence 0.5f
                )
            )
        )
        yield(
            TextRule(
                setOf(
                    SearchTerm.Plain("American Revolutionary War", 1f),
                    SearchTerm.Plain("American Revolution", 0.8f)
                ),
                Categorization(
                    period = Period.Range("American Revolutionary War", 1775, 1783) withConfidence 0.9f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.75f
                )
            )
        )
        yield(
            TextRule(
                setOf(
                    SearchTerm.Plain("Civil War", 1f),
                    SearchTerm.Plain("Gettysburg", 0.2f)
                ),
                Categorization(
                    period = civilWarPeriod withConfidence 0.9f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.25f
                )
            )
        )
        yield(
            TextRule(
                "Roman Empire",
                Categorization(period = Period.Range("Roman Empire", -27, 476))
            )
        )
        yield(
            TextRule(
                setOf(
                    SearchTerm.Plain("Jesus's life", 1f),
                    SearchTerm.Plain("Life of Jesus", 1f),
                ),
                Categorization(period = Century(1).period withConfidence 0.2f)
            )
        )
        yield(
            TextRule(
                "Westerns",
                Categorization(
                    period = Century(19).period withConfidence 0.2f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.2f
                )
            )
        )
        yield(
            TextRule(
                "Ancient History",
                Categorization(
                    period = Period.Range("Ancient history", -5000, 476) withConfidence 0.25f
                )
            )
        )
        yield(HandwrittenCategorizationRule())
        yield(GenghisKhanRule)
    }.toList()

    override fun begin() {
        println("${rules.size} rules loaded")
    }

    private val defaultCategorization = Categorization(
        Period.Unknown withConfidence 0f,
        Place.Unknown withConfidence 0f
    )

    override fun categorize(book: BookMetadata): CategorizedBook {
        val realCandidates = rules.mapNotNull { it.apply(book) }
        val candidates = realCandidates + defaultCategorization

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
        return CategorizedBook(book.book, period, place, realCandidates)
    }
}
