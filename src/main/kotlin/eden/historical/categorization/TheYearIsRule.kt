package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : Rule {
    private val phraseRegex = Regex("""(?:the\s+year\s+is|it\s+is)\s+(\d{4})""")
    private val anyYearRegex = Regex("""\W(\d{4})\W""")
    private val decadeRegex = Regex("""in the (?:early|late)?\s*(\d{4})s""")

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

        val decadeMatch = decadeRegex.find(book.synopsis)
        if (decadeMatch != null) {
            val decade = decadeMatch.groups[1]!!.value.toInt()
            return Categorization(Period("${decade}s", decade, decade + 10))
        }

        return null
    }
}