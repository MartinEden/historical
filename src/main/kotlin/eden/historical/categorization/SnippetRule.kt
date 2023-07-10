package eden.historical.categorization

import eden.historical.models.BookMetadata

data class SnippetRule(
    val matchingSnippets: Set<String>,
    val categorization: Categorization
) : RegexRule(synopsisRegex(matchingSnippets)) {

    constructor(snippet: String, categorization: Categorization)
            : this(setOf(snippet), categorization)

    override fun handleMatch(match: MatchResult, book: BookMetadata, fullText: String) = categorization

    companion object {
        fun synopsisRegex(snippets: Set<String>): Regex {
            val snippetRegexes = snippets.joinToString("|") { "(${it.lowercase()})" }
            // This tries to find the sequence of characters as a whole word (rather than finding 'Oman' in 'Romance').
            // On either side of the word we look for either a non-alphabetic character (so a space or a punctuation mark)
            // or for the beginning/end of the string.
            return Regex("([^a-zA-Z]|^)$snippetRegexes([^a-zA-Z]|$)")
        }
    }
}