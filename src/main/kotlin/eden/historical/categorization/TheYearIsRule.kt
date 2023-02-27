package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : Rule {
    private val regex = Regex("""(?:The\s+year\s+is|it\s+is)\s+(\d{4})""")

    override fun apply(book: BookMetadata): Categorization? {
        val match = regex.find(book.synopsis)
        return if (match != null) {
            val year = match.groups[1]!!.value.toInt()
            Categorization(Period(year.toString(), year, year))
        } else null
    }
}