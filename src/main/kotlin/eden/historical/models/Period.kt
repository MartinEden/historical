package eden.historical.models

data class Period(
    val name: String,
    val start: Int,
    val end: Int
) {
    companion object {
        val default = Period("Unknown", -4000, 2000)
    }

    override fun toString(): String {
        return if (start != end) {
            "$name ($start - $end)"
        } else {
            "$name ($start)"
        }
    }
}
