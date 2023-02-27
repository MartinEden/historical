package eden.historical.sources

import eden.historical.fetching.Fetcher

class GoodreadsUrlFinder(private val fetcher: Fetcher) {
    private val baseUrl = "$goodreadsUrl/shelf/show/historical-fiction"

    val urls: Sequence<String> = sequence {
        val rootPage = fetcher.get(baseUrl)
        val bookAnchors = rootPage.select(".mainContent a.bookTitle")

        yieldAll(bookAnchors.map { goodreadsUrl + it.attr("href") })

        // TODO: Process multiple pages of results
    }
}