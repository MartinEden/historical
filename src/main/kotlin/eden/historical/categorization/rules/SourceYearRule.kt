package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata
import eden.historical.models.Period

object SourceYearRule : Rule {
    override fun apply(book: BookMetadata): AppliedCategorization? {
        val year = book.years.firstOrNull()
        return if (year != null) {
            Categorization(period = Period.Range(year.toString(), year, year))
                .withReasoning(this, "Year data from Goodreads: ${book.years}")
        } else null
    }
}