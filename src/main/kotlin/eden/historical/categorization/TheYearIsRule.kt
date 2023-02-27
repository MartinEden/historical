package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : Rule {
    private val phraseRegex = Regex("""(?:the\s+year\s+is|it\s+is)\s+(\d{4})""")
    private val anyYearRegex = Regex("""\W(\d{4})\W""")

    override fun apply(book: BookMetadata): Categorization? {
        val phraseMatch = phraseRegex.find(book.synopsis)
        if (phraseMatch != null) {
            val year = phraseMatch.groups[1]!!.value.toInt()
            return Categorization(Period(year.toString(), year, year))
        }

        val anyMatch = anyYearRegex.find(book.synopsis)
        if (anyMatch != null) {
            val year = anyMatch.groups[1]!!.value.toInt()
            return Categorization(Period(year.toString(), year, year))
        }

        return null
    }
}