package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata
import eden.historical.models.Period

object SourceYearRule : Rule {
    override fun apply(book: BookMetadata): Iterable<AppliedCategorization> {
        return book.years.map { year ->
            Categorization(period = Period.Range(year.toString(), year, year))
                .withReasoning(this, "Year data from Goodreads: ${book.years}")
        }
    }
}