package eden.historical.categorization

import eden.historical.models.BookMetadata

class TextRule(termSet: Set<SearchTerm>, private val categorization: Categorization) : Rule {
    private val terms = termSet.sortedByDescending { it.confidenceMultiplier }

    constructor(snippet: SearchTerm, categorization: Categorization)
            : this(setOf(snippet), categorization)

    override fun apply(book: BookMetadata): Categorization? {
        for (term in terms) {
            val termCategorization = categorization.weightedBy(term.confidenceMultiplier)
            if (term.text in book.tags || term.text in book.places) {
                return termCategorization
            } else if (term.regex in book.synopsis) {
                return termCategorization.weightedBy(0.25f)
            } else if (book.reviews.any { term.regex in it }) {
                return termCategorization.weightedBy(0.1f)
            }
        }
        return null
    }
}

data class SearchTerm(val text: String, val confidenceMultiplier: Float) {
    val regex = Regex("([^a-zA-Z]|^)${text.lowercase()}([^a-zA-Z]|$)")
}