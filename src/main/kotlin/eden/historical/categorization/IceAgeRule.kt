package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object IceAgeRule : SynopsisRegexRule(Regex("Ice Age", RegexOption.IGNORE_CASE)) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        return Categorization(Period("Ice Age", -115000, -11700))
    }
}