package eden.historical

import eden.historical.categorization.HardcodedCategorizer
import eden.historical.fetching.CachingFetcher
import eden.historical.sources.GoodreadsSource
import eden.historical.storage.DummyStore

fun main() {
    val source = GoodreadsSource(CachingFetcher())
    val categorizer = HardcodedCategorizer()
    val store = DummyStore()
    val job = NovelProcessingJob(source, categorizer, store)
    job.run()
}