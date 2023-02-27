package eden.historical

import eden.historical.categorization.Categorizer
import eden.historical.sources.BookSource
import eden.historical.storage.Store

class NovelProcessingJob(
    private val bookSource: BookSource,
    private val categorizer: Categorizer,
    private val store: Store
) {
    fun run() {
        for (book in bookSource.books) {
            println(book.tags.joinToString())
            val categorizedBook = categorizer.categorize(book)
            store.put(categorizedBook)
        }
        store.save()
    }
}
