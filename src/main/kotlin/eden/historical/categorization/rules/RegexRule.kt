package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.models.BookMetadata

abstract class RegexRule(private val regex: Regex) : Rule {
//    init {
//        println(regex.pattern)
//    }

    override fun apply(book: BookMetadata): AppliedCategorization? {
        val match = regex.find(book.synopsis)
        if (match != null) {
            return handleMatch(match, book, book.synopsis)
        } else {
            for (review in book.reviews) {
                val reviewMatch = regex.find(review)
                if (reviewMatch != null) {
                    return handleMatch(reviewMatch, book, review)?.weightedBy(0.1f)
                }
            }
        }
        return null
    }

    protected abstract fun handleMatch(
        match: MatchResult,
        book: BookMetadata,
        fullText: String    // This is the full string that was searched to get this MatchResult
    ): AppliedCategorization?
}