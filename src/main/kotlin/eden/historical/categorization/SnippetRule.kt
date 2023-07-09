package eden.historical.categorization

import eden.historical.models.BookMetadata

data class SnippetRule(
    val matchingSnippets: Set<String>,
    val categorization: Categorization
) : SynopsisRegexRule(synopsisRegex(matchingSnippets)) {

    constructor(snippet: String, categorization: Categorization)
            : this(setOf(snippet), categorization)

    override fun handleMatch(match: MatchResult, book: BookMetadata) = categorization

    companion object {
        fun synopsisRegex(snippets: Set<String>) = Regex(snippets.joinToString("|") { "(${asWordRegex(it)})" })

        private fun asWordRegex(str: String) = "([^a-zA-Z]|^)${str.lowercase()}([^a-zA-Z]|$)"
    }
}