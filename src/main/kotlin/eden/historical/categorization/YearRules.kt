package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : SynopsisRegexRule(Regex("""(?:the\s+year\s+is|it\s+is)\s+(\d{4})""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        val year = match.groups[1]!!.value.toInt()
        return Categorization(Period.Range(year.toString(), year, year))
    }
}

object AnyYearRule : SynopsisRegexRule(Regex("""\W(\d{4})\W""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        val year = match.groups[1]!!.value.toInt()
        return Categorization(Period.Range(year.toString(), year, year))
    }
}

object DecadeRule : SynopsisRegexRule(Regex("""in the (?:early|late)?\s*(\d{4})s""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        val decade = match.groups[1]!!.value.toInt()
        return Categorization(Period.Range("${decade}s", decade, decade + 10))
    }
}
