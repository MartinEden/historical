package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object SourceYearRule : Rule {
    override fun apply(book: BookMetadata): Categorization? {
        val year = book.years.firstOrNull()
        return if (year != null) {
            Categorization(period = Period.Range(year.toString(), year, year))
        } else null
    }
}