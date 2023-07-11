package eden.historical.categorization.rules

import eden.historical.categorization.Categorization
import eden.historical.models.BookMetadata

open class TextRule(terms: Set<SearchTerm>, private val categorization: Categorization) : Rule {
    private val terms = terms.sortedByDescending { it.confidenceMultiplier }

    constructor(term: String, categorization: Categorization)
            : this(setOf(SearchTerm(term, 1f)), categorization)

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
        return if (term.isInSet(book.tags) || term.isInSet(book.places)) {
            1f
        } else if (term.isInFreeText(book.synopsis)) {
            0.25f
        } else if (book.reviews.any { term.isInFreeText(it) }) {
            0.1f
        } else {
            0f
        }
    }
}

class SearchTerm(private val text: String, val confidenceMultiplier: Float) {
    private val regex = Regex("([^a-zA-Z]|^)${text.lowercase()}([^a-zA-Z]|$)")

    fun isInSet(set: Set<String>) = text in set
    fun isInFreeText(text: String) = regex in text
}