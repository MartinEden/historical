package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Century
import eden.historical.models.Period

class CenturyRule(private val century: Century) : Rule {
    private val regex = Regex("""the ${century.ordinalAsWord} century""")
    private val nextCenturyRegex = makeNextCenturyRegex()

    override fun apply(book: BookMetadata): Categorization? {
        return if (book.tags.any { it == "${century.ordinalAsNumber} Century" }) {
            Categorization(period = century.period)
        } else if (regex.containsMatchIn(book.synopsis)) {
            Categorization(period = century.period withConfidence 0.5f)
        } else if (nextCenturyRegex != null && nextCenturyRegex in book.synopsis) {
            Categorization(
                period = Period.Range(
                    nextCenturyRegex.pattern,
                    century.period.start,
                    century.next!!.period.end
                ) withConfidence 0.5f
            )
        } else null
    }

    private fun makeNextCenturyRegex(): Regex? {
        val nextCentury = century.next
        return if (nextCentury != null) {
            Regex("""the ${century.ordinalAsNumber} and ${nextCentury.ordinalAsNumber} centuries""")
        } else {
            null
        }
    }

    companion object {
        val all = sequence {
            for (century in 1..21) {
                yield(CenturyRule(Century(century)))
            }
        }
    }
}