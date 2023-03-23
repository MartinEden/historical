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

        override fun toString(): String {
            return if (start != end) {
                "$name ($startAsYear - $endAsYear)"
            } else {
                "$name ($startAsYear)"
            }
        }
    }
}

fun Int.yearToString() = if (this > 0) {
    this.toString()
} else {
    "${this.absoluteValue}BC"
}