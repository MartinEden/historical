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
            val categorizedBook = categorizer.categorize(book)
            store.put(categorizedBook)
        }
        // TODO: Put this in a try... finally so that we save even if an exception occurs
        store.save()
    }
}
