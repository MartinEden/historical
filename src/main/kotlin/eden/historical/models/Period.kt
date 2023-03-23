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
        val size by lazy {
            end - start
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
                x is Period.Range && y is Period.Range -> x.size - y.size
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