package eden.historical.models

data class CategorizedBook(
    val book: Book,
    val period: Period,
    val place: Place    // TODO: This should be a collection of Places
) {
    val title
        get() = book.title
    val authors
        get() = book.authors
}
