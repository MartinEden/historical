package eden.historical.categorization.rules

import eden.historical.categorization.AppliedCategorization
import eden.historical.categorization.Categorization
import eden.historical.categorization.getSurroundingContext
import eden.historical.categorization.withReasoning
import eden.historical.models.BookMetadata

open class TextRule(terms: Set<SearchTerm>, private val categorization: Categorization) : Rule {
    private val terms = terms.sortedByDescending { it.confidenceMultiplier }

    constructor(term: String, categorization: Categorization) : this(setOf(SearchTerm(term, 1f)), categorization)

    override fun apply(book: BookMetadata): Iterable<AppliedCategorization> {
        return terms.flatMap { applyTerm(it, book) }
    }

    private fun applyTerm(term: SearchTerm, book: BookMetadata): Sequence<AppliedCategorization> {
        val rule = this
        val cat = categorization.weightedBy(term.confidenceMultiplier)
        return sequence {
            if (term.text in book.tags) {
                yield(
                    cat.withReasoning(rule, "Term ($term) is in tags: ${book.tags}")
                )
            }
            if (term.text in book.places) {
                yield(
                    cat.withReasoning(rule, "Term ($term) is in place data: ${book.places}")
                )
            }
//            if (term.text in book.book.title) {
//                return cat
//                    .weightedBy(0.5f)
//                    .withReasoning(this, "Term ($term) is in title: ${book.book.title}")
//            }

            val match = term.regex.find(book.synopsis)
            if (match != null) {
                yield(
                    cat.weightedBy(0.25f).withReasoning(
                        rule, "Term ($term) is in synopsis: ${match.getSurroundingContext(book.synopsis)}"
                    )
                )
            }

            yieldAll(book.reviews.mapNotNull { review ->
                val reviewMatch = term.regex.find(review)
                if (reviewMatch != null) {
                    cat.weightedBy(0.1f)
                        .withReasoning(rule, "Term ($term) is in review: ${reviewMatch.getSurroundingContext(review)}")
                } else null
            })
        }
    }
}

class SearchTerm(val text: String, val confidenceMultiplier: Float) {
    val regex = Regex("([^a-zA-Z]|^)${text.lowercase()}([^a-zA-Z]|$)")

    override fun toString() = text
}