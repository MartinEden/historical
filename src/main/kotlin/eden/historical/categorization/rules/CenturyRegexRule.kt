package eden.historical.categorization.rules

import eden.historical.categorization.Categorization
import eden.historical.categorization.withConfidence
import eden.historical.models.BookMetadata
import eden.historical.models.Century

class CenturyRegexRule : RegexRule(buildRegex()) {
    private val lookup: Map<String, Century> = sequence {
        for (century in Century.ofInterest) {
            yield(century.ordinalAsWord to century)
            yield(century.ordinalAsNumber to century)
        }
    }.toMap()

    override fun handleMatch(match: MatchResult, book: BookMetadata, fullText: String): Categorization {
        val century = lookup[match.value]
            ?: throw Exception("Unable to find century '${match.value}' in lookup")
        return Categorization(period = century.period withConfidence 0.75f)
    }

    companion object {
        fun buildRegex(): Regex {
            val terms = Century.ofInterest.map { "(?:${it.ordinalAsWord}|${it.ordinalAsNumber})" }
            val baseRegex = terms.joinToString("|") { it.lowercase() }
            return Regex("(?<![0-9])($baseRegex)(?=(?: |-)century)")
        }
    }
}