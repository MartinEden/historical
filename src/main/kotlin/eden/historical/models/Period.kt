package eden.historical.models

import kotlin.math.absoluteValue

sealed class Period {
    object Unknown : Period() {
        override fun toString() = "Unknown"
    }
    data class Range(
        val name: String,
        val start: Int,
        val end: Int
    ): Period() {

        private val startAsYear
            get() = start.yearToString()
        private val endAsYear
            get() = end.yearToString()
        val lengthInYears: Int

        init {
            var length = end - start
            // There is no zero AD, so adjust length accordingly
            if (start < 0 && end > 0) {
                length -= 1
            }
            lengthInYears = length
        }

        override fun toString(): String {
            return if (start != end) {
                "$name ($startAsYear - $endAsYear)"
            } else {
                "$name ($startAsYear)"
            }
        }
    }

    class Specificity : Comparator<Period> {
        // If x is more specific return a negative number
        // If y is more specific return a positive number
        override fun compare(x: Period, y: Period): Int {
            return when {
                x is Period.Range && y is Period.Range -> x.lengthInYears - y.lengthInYears
                x is Period.Unknown && y is Period.Range -> +1
                x is Period.Range && y is Period.Unknown -> -1
                else -> 0
            }
        }
    }
}

fun Int.yearToString() = if (this > 0) {
    this.toString()
} else {
    "${this.absoluteValue}BC"
}