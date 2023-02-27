package eden.historical.models

data class Book(
    val id: String,
    val title: String,
    val authors: List<Author>
) : Comparable<Book> {

    override fun compareTo(other: Book): Int {
        var i = 0
        while (i < authors.size && i < other.authors.size) {
            val x = authors[i].compareTo(other.authors[i])
            if (x != 0) {
                return x
            }
            i += 1
        }
        if (authors.size != other.authors.size) {
            return authors.size - other.authors.size
        }
        return title.compareTo(other.title)
    }
}