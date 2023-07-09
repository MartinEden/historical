package eden.historical.categorization

import eden.historical.models.BookMetadata
import eden.historical.models.Period

object TheYearIsRule : SynopsisRegexRule(Regex("""(?:the\s+year\s+is|it\s+is)\s+(\d{2,4})""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        val year = match.groups[1]!!.value.toInt()
        return Categorization(Period.Range(year.toString(), year, year) withConfidence 0.75f)
    }
}

object AnyYearRule : SynopsisRegexRule(Regex("""\W(\d{4})\W""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        val year = match.groups[1]!!.value.toInt()
        val winnerRegex = Regex("""winner of [^.]*$year [^.]*prize""")
        return if (winnerRegex in book.synopsis) {
            println("Skipping year because it is describing the year the book won a prize")
            null
        } else {
            return Categorization(Period.Range(year.toString(), year, year) withConfidence 0.25f)
        }
    }
}

// TODO: Currently we treat centuries (e.g. 1900s as if they were just the first decade
object DecadeRule : SynopsisRegexRule(Regex("""(?<phrasing>in the (?:early|late)?)?\s*(?<decade>\d{4})s""")) {
    override fun handleMatch(match: MatchResult, book: BookMetadata): Categorization? {
        val decade = match.groups["decade"]!!.value.toInt()
        val phrasingPresent = match.groups["phrasing"] != null
        val confidence = if (phrasingPresent) 0.7f else 0.35f
        return Categorization(Period.Range("${decade}s", decade, decade + 10) withConfidence confidence)
    }
}
