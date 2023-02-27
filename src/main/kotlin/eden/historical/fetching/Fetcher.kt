package eden.historical.fetching

import org.jsoup.nodes.Document

interface Fetcher {
    fun get(url: String): Document
}