package eden.historical

import eden.historical.categorization.RuleBasedCategorizer
import eden.historical.fetching.CachingFetcher
import eden.historical.fetching.JsoupFetcher
import eden.historical.fetching.RequiresLoginFetcher
import eden.historical.fetching.ThrottlingFetcher
import eden.historical.models.countries.Country
import eden.historical.models.countries.CountryDataSource
import eden.historical.sources.GoodreadsSource
import eden.historical.storage.HumanReadableStore
import eden.historical.storage.JsonStore
import eden.historical.storage.MultiStore

fun main() {
    val fetcher = CachingFetcher(ThrottlingFetcher(RequiresLoginFetcher(JsoupFetcher())))
    val source = GoodreadsSource(fetcher)
    println("Loading country data")
                                                 // TODO: Add the counties of England properly
    val countries = CountryDataSource().load() + Country(listOf("Yorkshire"), null, emptyList())
    println("Beginning categorization")
    val categorizer = RuleBasedCategorizer(countries)
    val store = MultiStore(listOf(
        JsonStore(),
        HumanReadableStore()
    ))
    val job = NovelProcessingJob(source, categorizer, store)
    job.run()
}