package eden.historical.categorization

import eden.historical.categorization.rules.*
import eden.historical.models.*
import eden.historical.models.countries.Country

class RuleBasedCategorizer(countries: List<Country>) : Categorizer {
    private val rules = sequence {
        yield(DefaultRule)

        yield(SourceYearRule)
        yield(TheYearIsRule)
        yield(AnyYearRule)
        yield(DecadeRule)
        yield(CenturyTagRule)
        yield(CenturyRegexRule())

        yield(LocationSourceDataRule(countries))
        yield(LocationRegexRule(countries))
        yield(LocationRegexRule(listOf(Country(listOf("Yorkshire"), null, emptyList()))))

        yield(
            TextRule(
                setOf(
                    SearchTerm("World War II", 1f),
                    SearchTerm("WWII", 0.35f),
                    SearchTerm("Nazi", 0.25f)
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
                Categorization(period = Period.Range("Great Depression", 1929, 1939))
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
                    SearchTerm("American Revolutionary War", 1f),
                    SearchTerm("American Revolution", 0.8f)
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
                    SearchTerm("Civil War", 1f),
                    SearchTerm("Gettysburg", 0.2f)
                ),
                Categorization(
                    period = Period.Range("American Civil War", 1861, 1865) withConfidence 0.9f,
                    place = countries.single { it.iso3 == "USA" }.asPlace() withConfidence 0.25f
                )
            )
        )
        yield(
            TextRule(
                "Roman Empire",
                Categorization(
                    period = Period.Range("Roman Empire", -27, 476) withConfidence 1f,
                    place = Place.Area("Roman Empire", coords = emptyList()) withConfidence 0.75f
                )
            )
        )
        yield(
            TextRule(
                setOf(
                    SearchTerm("Jesus's life", 1f),
                    SearchTerm("Life of Jesus", 1f),
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
    }.toList()

    override fun begin() {
        println("${rules.size} rules loaded")
//        println("By type, there are:")
//        for (group in rules.groupBy { it::class }.entries.sortedByDescending { it.value.size }) {
//            println("${group.key.simpleName}: ${group.value.size}")
//        }
    }

    override fun categorize(book: BookMetadata): CategorizedBook {
        val candidates = rules.flatMap { it.apply(book) }

        // TODO: take smallest intersection? e.g. 15th century / tudor
        val period = candidates
            .mapNotNull { it.categorization.period }
            .highestConfidenceInfo()
            .sortedWith(Period.Specificity())
            .firstOrNull() ?: Period.Unknown

        // TODO: distinguish places by specificity
        // TODO: merge places when there are multiple conflicting places of equal weight. e.g. multiple states -> USA
        val place = candidates
            .mapNotNull { it.categorization.place }
            .highestConfidenceInfo()
            .first()

        val alternativeCategorizations = candidates
            .filter { it.categorization.totalConfidence > 0 }
            .sortedByDescending { it.categorization.totalConfidence }

        return CategorizedBook(book.book, period, place, alternativeCategorizations)
    }
}
