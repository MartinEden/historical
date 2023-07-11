package eden.historical.categorization.rules

import eden.historical.categorization.*
import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : RegexRule(Regex("""(?<!written )(?:the year is|it is|in the year)\s+(\d{2,4})""")) {
    override fun handleMatch(match: MatchResult, fullText: String): Categorization {
        val year = match.groups[1]!!.value.toInt()
        return Categorization(Period.Range(year.toString(), year, year))
    }
}

object AnyYearRule : RegexRule(Regex("""\W(\d{4})\W""")) {
    override fun handleMatch(match: MatchResult, fullText: String): Categorization {
        val year = match.groups[1]!!.value.toInt()
        var categorization = Categorization(Period.Range(year.toString(), year, year) withConfidence 0.25f)

        val redHerringRegexes = listOf(
            Regex("""winner of [^.]*$year [^.]*prize"""),
            Regex("""(written|published) in (the year )?$year"""),
        )
        if (redHerringRegexes.any { it in fullText }) {
            // TODO: Would be good to be able to output reasoning here
            categorization = categorization.weightedBy(0.001f)
        }
        return categorization
    }
}

// It's okay that this matches centuries (e.g. 1700s) as it has a lower confidence than the century rules
object DecadeRule : RegexRule(Regex("""(?<phrasing>in the (?:early|late)?)?\s*(?<decade>\d{4})s""")) {
    override fun handleMatch(match: MatchResult, fullText: String): Categorization {
        val decade = match.groups["decade"]!!.value.toInt()
        val phrasingPresent = match.groups["phrasing"] != null
        val confidence = if (phrasingPresent) 0.7f else 0.35f
        return Categorization(Period.Range("${decade}s", decade, decade + 10) withConfidence confidence)
    }
}
