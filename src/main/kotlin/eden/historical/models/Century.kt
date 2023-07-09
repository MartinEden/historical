package eden.historical.models

data class Century(private val century: Int) {
    init {
        if (century < 1 || century > ordinals.size) {
            throw Exception("Unsupported century: $century")
        }
    }

    val ordinalAsWord = ordinals[century - 1]
    val ordinalAsNumber = ordinalWithSuffix(century)
    private val startYear = ((century - 1) * 100)
        .coerceAtLeast(1) // There is no 0 AD
    val period = Period.Range("$ordinalAsNumber Century", startYear, startYear + 99)

    val next by lazy {
        if (century + 1 <= ordinals.size) {
            Century(century + 1)
        } else {
            null
        }
    }

    private fun ordinalWithSuffix(number: Int): String = when (number) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${number}th"
    }

    companion object {
        val ordinals = listOf(
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth",
            "twentieth",
            "twenty-first"
        )
    }
}