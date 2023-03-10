package eden.historical.sources

import eden.historical.fetching.Fetcher

class GoodreadsUrlFinder(private val fetcher: Fetcher) {
    private val baseUrl = "$goodreadsUrl/shelf/show/historical-fiction"

    val urls: Sequence<String> = sequence {
        var pageNumber = 1
        while (!fetcher.exhausted) {
            // TODO: Need to be logged in to fetch more than the first page
            val indexPage = fetcher.get("$baseUrl?page=$pageNumber")
            val bookAnchors = indexPage.select(".mainContent a.bookTitle")
            yieldAll(bookAnchors.map { goodreadsUrl + it.attr("href") })
            pageNumber++
        }
    }
}