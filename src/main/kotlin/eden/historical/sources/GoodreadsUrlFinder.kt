package eden.historical.sources

import eden.historical.fetching.Fetcher

class GoodreadsUrlFinder(private val fetcher: Fetcher) {
    private val baseUrl = "$goodreadsUrl/shelf/show/historical-fiction"

    val urls: Sequence<String> = sequence {
        var pageNumber = 1
        while (!fetcher.exhausted) {
            val indexPage = fetcher.get("$baseUrl?page=$pageNumber", emptyMap())!!
            val bookAnchors = indexPage.select(".mainContent a.bookTitle")
            for (anchor in bookAnchors) {
                if (fetcher.exhausted) {
                    break
                }
                yield(goodreadsUrl + anchor.attr("href"))
            }
            pageNumber++
        }
    }
}