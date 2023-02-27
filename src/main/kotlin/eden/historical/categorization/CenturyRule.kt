package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

class CenturyRule(name: String, private val period: Period) : Rule {
    private val regex = Regex("""the $name century""")

    override fun apply(book: BookMetadata): Categorization? {
        return if (regex.containsMatchIn(book.synopsis)) {
            Categorization(period = period)
        } else null
    }

    companion object {
        val all by lazy {
            var startYear = 0
            ordinals.map {
                val capitalized = it.replaceFirstChar { it.uppercase() }
                val rule = CenturyRule(it, Period("$capitalized century", startYear.coerceAtLeast(1), startYear + 99))
                startYear += 100
                rule
            }
        }

        private val ordinals = listOf(
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