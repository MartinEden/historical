package eden.historical.models

data class BookMetadata(
    val book: Book,
    val synopsis: String,
    val reviews: List<String>,
    val tags: Set<String>,
    val places: Set<String>,
    val years: Set<Int>
)
