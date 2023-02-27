package eden.historical.models

data class CategorizedBook(
    val book: Book,
    val period: Period,
    val location: Place
) {
    val title
        get() = book.title
    val authors
        get() = book.authors
}
