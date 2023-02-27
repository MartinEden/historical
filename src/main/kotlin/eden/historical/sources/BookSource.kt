package eden.historical.sources

import eden.historical.models.BookMetadata

interface BookSource {
    val books: Sequence<BookMetadata>
}