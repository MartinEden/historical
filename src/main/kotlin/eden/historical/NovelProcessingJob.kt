package eden.historical

import eden.historical.categorization.Categorizer
import eden.historical.sources.BookSource
import eden.historical.storage.Store
import java.time.Duration

class NovelProcessingJob(
    private val bookSource: BookSource,
    private val categorizer: Categorizer,
    private val store: Store
) {
    fun run() {
        val progress = ProgressReporter(reportFrequency = Duration.ofSeconds(2))
        try {
            categorizer.begin()
            for ((i, book) in bookSource.books.withIndex()) {
                progress.update("Processed $i books")
                val categorizedBook = categorizer.categorize(book)
                store.put(categorizedBook)
            }
        } finally {
            store.save()
        }
    }
}
