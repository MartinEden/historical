package eden.historical.models

import eden.historical.categorization.Categorization

data class CategorizedBook(
    val book: Book,
    val period: Period,
    val place: Place,    // TODO: This should be a collection of Places
    val alternativeCategorizations: List<Categorization>,
) {
    val title
        get() = book.title
    val authors
        get() = book.authors
}
