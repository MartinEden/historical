package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.getSurroundingContext
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata

open class TextRule(terms: Set<SearchTerm>, private val categorization: Categorization) : Rule {
    private val terms = terms.sortedByDescending { it.confidenceMultiplier }

    constructor(term: String, categorization: Categorization)
            : this(setOf(SearchTerm(term, 1f)), categorization)

    // TODO: Consider returning multiple categorizations, and later we can sort them out by confidence/specificity
    override fun apply(book: BookMetadata): AppliedCategorization? {
        for (term in terms) {
            val cat = categorization.weightedBy(term.confidenceMultiplier)
            if (term.text in book.tags) {
                return cat
                    .withReasoning(this, "Term ($term) is in tags: ${book.tags}")
            }
            if (term.text in book.places) {
                return cat
                    .withReasoning(this, "Term ($term) is in place data: ${book.places}")
            }

            val match = term.regex.find(book.synopsis)
            if (match != null) {
                return cat
                    .weightedBy(0.25f)
                    .withReasoning(this, "Term ($term) is in synopsis: ${match.getSurroundingContext(book.synopsis)}")
            }

            for (review in book.reviews) {
                val reviewMatch = term.regex.find(review)
                if (reviewMatch != null) {
                    return cat
                        .weightedBy(0.1f)
                        .withReasoning(this, "Term ($term) is in review: ${reviewMatch.getSurroundingContext(review)}")
                }
            }
        }
        return null
    }
}

class SearchTerm(val text: String, val confidenceMultiplier: Float) {
    val regex = Regex("([^a-zA-Z]|^)${text.lowercase()}([^a-zA-Z]|$)")

    override fun toString() = text
}