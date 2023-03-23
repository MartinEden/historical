package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

class CenturyRule(century: Int) : Rule {
    init {
        if (century < 1 || century > ordinals.size) {
            throw Exception("Unsupported century for CenturyRule: $century")
        }
    }

    private val ordinalAsWord = ordinals[century - 1]
    private val ordinalAsNumber = ordinalWithSuffix(century)
    private val startYear = ((century - 1) * 100)
        .coerceAtLeast(1) // There is no 0 AD
    private val period = Period.Range("$ordinalAsNumber Century", startYear, startYear + 99)
    private val regex = Regex("""the $ordinalAsWord century""")

    override fun apply(book: BookMetadata): Categorization? {
        return if (book.tags.any { it == "$ordinalAsNumber Century" } || regex.containsMatchIn(book.synopsis)) {
            Categorization(period = period)
        } else null
    }

    private fun ordinalWithSuffix(number: Int): String = when (number) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${number}th"
    }

    companion object {
        val all = sequence {
            for (century in 1..21) {
                yield(CenturyRule(century))
            }
        }

        val ordinals = listOf(
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth",
            "twentieth",
            "twenty-first"
        )
    }
}