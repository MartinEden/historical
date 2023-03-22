package eden.historical.categorization

import eden.historical.models.BookMetadata

abstract class SynopsisRegexRule(private val regex: Regex) : Rule {
    override fun apply(book: BookMetadata): Categorization? {
        val match = regex.find(book.synopsis)
        return if (match != null) {
            handleMatch(match, book)
        } else null
    }
    protected abstract fun handleMatch(match: MatchResult, book: BookMetadata): Categorization?
}