package eden.historical.categorization

import eden.historical.models.BookMetadata

class TextRule(termSet: Set<SearchTerm>, private val categorization: Categorization) : Rule {
    private val terms = termSet.sortedByDescending { it.confidenceMultiplier }

    constructor(snippet: SearchTerm, categorization: Categorization)
            : this(setOf(snippet), categorization)

    // TODO: Consider returning multiple categorizations, and later we can sort them out by confidence/specificity
    override fun apply(book: BookMetadata): Categorization? {
        for (term in terms) {
            val support = getSupportForTerm(term, book)
            if (support > 0) {
                                                           // we trust some terms more than others
                return categorization.weightedBy(support * term.confidenceMultiplier)
            }
        }
        return null
    }

    // We trust some data more than other
    private fun getSupportForTerm(term: SearchTerm, book: BookMetadata): Float {
        return if (term.text in book.tags || term.text in book.places) {
            1f
        } else if (term.regex in book.synopsis) {
            0.25f
        } else if (book.reviews.any { term.regex in it }) {
            0.1f
        } else {
            0f
        }
    }
}

data class SearchTerm(val text: String, val confidenceMultiplier: Float) {
    val regex = Regex("([^a-zA-Z]|^)${text.lowercase()}([^a-zA-Z]|$)")
}