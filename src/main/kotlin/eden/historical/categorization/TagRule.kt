package eden.historical.categorization

import eden.historical.models.BookMetadata

class TagRule(private val matchesTags: Set<String>, private val categorization: Categorization) : Rule {
    constructor(matchTag: String, categorization: Categorization)
            : this(setOf(matchTag), categorization)

    override fun apply(book: BookMetadata): Categorization? {
        return if (book.tags.intersect(matchesTags).any()) {
            categorization
        } else null
    }
}