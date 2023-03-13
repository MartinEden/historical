package eden.historical

import eden.historical.categorization.HardcodedCategorizer
import eden.historical.fetching.CachingFetcher
import eden.historical.fetching.JsoupFetcher
import eden.historical.fetching.RequiresLoginFetcher
import eden.historical.fetching.ThrottlingFetcher
import eden.historical.sources.GoodreadsSource
import eden.historical.storage.HumanReadableStore
import eden.historical.storage.JsonStore
import eden.historical.storage.MultiStore

fun main() {
    val fetcher = CachingFetcher(ThrottlingFetcher(RequiresLoginFetcher(JsoupFetcher())))
    val source = GoodreadsSource(fetcher)
    val categorizer = HardcodedCategorizer()
    val store = MultiStore(listOf(
        JsonStore(),
        HumanReadableStore()
    ))
    val job = NovelProcessingJob(source, categorizer, store)
    job.run()
}