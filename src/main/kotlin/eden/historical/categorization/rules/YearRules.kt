package eden.historical.categorization.rules

import eden.historical.categorization.Categorization
import eden.historical.categorization.withConfidence
import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : RegexRule(Regex("""(?:the year is|it is|in the year)\s+(\d{2,4})""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata, fullText: String): Categorization {
        val year = match.groups[1]!!.value.toInt()
        return Categorization(Period.Range(year.toString(), year, year))
    }
}

object AnyYearRule : RegexRule(Regex("""\W(\d{4})\W""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata, fullText: String): Categorization? {
        val year = match.groups[1]!!.value.toInt()
        val redHerringRegexes = listOf(
            Regex("""winner of [^.]*$year [^.]*prize"""),
            Regex("""(written|published) in $year"""),
        )
        return if (redHerringRegexes.any { it in fullText }) {
            null
        } else {
            return Categorization(Period.Range(year.toString(), year, year) withConfidence 0.25f)
        }
    }
}

// TODO: Currently we treat centuries (e.g. 1900s) as if they were just the first decade
object DecadeRule : RegexRule(Regex("""(?<phrasing>in the (?:early|late)?)?\s*(?<decade>\d{4})s""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata, fullText: String): Categorization {
        val decade = match.groups["decade"]!!.value.toInt()
        val phrasingPresent = match.groups["phrasing"] != null
        val confidence = if (phrasingPresent) 0.7f else 0.35f
        return Categorization(Period.Range("${decade}s", decade, decade + 10) withConfidence confidence)
    }
}
