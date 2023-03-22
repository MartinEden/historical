package eden.historical.models

data class CategorizedBook(
    val book: Book,
    val period: Period,
    val place: Place
) {
    val title
        get() = book.title
    val authors
        get() = book.authors
}
