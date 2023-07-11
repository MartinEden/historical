package eden.historical.categorization

data class Reasoning(
    val rule: String,
    val relevantData: Any
)

fun MatchResult.getSurroundingContext(fullText: String): String {
    val start = (this.range.first - 40).coerceAtLeast(0)
    val end = (this.range.last + 40).coerceAtMost(fullText.length)
    return fullText.substring(start until end)
}