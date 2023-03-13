package eden.historical.models

data class Author(val lastName: String, val otherNames: String): Comparable<Author> {

    override fun compareTo(other: Author) = comparator.compare(this, other)
    override fun toString() = if (otherNames.isNotBlank()) {
        "$lastName, $otherNames"
    } else {
        lastName
    }

    companion object {
        val comparator = compareBy<Author>({ it.lastName }, { it.otherNames })

        fun fromFullName(fullName: String): Author {
            val regex = Regex("""\s*([^ ]+)$""")
            val match = regex.find(fullName)
            return if (match != null) {
                val group = match.groups[1]!!
                val otherNamesEnd = (group.range.first - 1).coerceAtLeast(0)
                Author(group.value, fullName.substring(0 until otherNamesEnd))
            } else {
                Author(fullName, "")
            }
        }
    }
}