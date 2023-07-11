package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.getSurroundingContext
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata

abstract class RegexRule(private val regex: Regex) : Rule {
//    init {
//        println(regex.pattern)
//    }

    override fun apply(book: BookMetadata): Iterable<AppliedCategorization> {
        val rule = this
        return sequence {
            val match = regex.find(book.synopsis)
            if (match != null) {
                yield(
                    handleMatch(match, book.synopsis)
                        ?.withReasoning(rule, "Found in synopsis: " + match.getSurroundingContext(book.synopsis))
                )
            }
            yieldAll(book.reviews.mapNotNull(::applyToReview))
        }.filterNotNull().toList()
    }


    private fun applyToReview(review: String): AppliedCategorization? {
        val reviewMatch = regex.find(review)
        return if (reviewMatch != null) {
            handleMatch(reviewMatch, review)
                ?.weightedBy(0.1f)
                ?.withReasoning(this, "Found in review: " + reviewMatch.getSurroundingContext(review))
        } else null
    }

    protected abstract fun handleMatch(
        match: MatchResult,
        fullText: String    // This is the full string that was searched to get this MatchResult
    ): Categorization?
}