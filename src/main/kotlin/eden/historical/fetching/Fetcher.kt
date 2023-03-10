package eden.historical.fetching

import org.jsoup.nodes.Document

interface Fetcher {
    fun get(url: String, cookies: Map<String, String>): Document
    val exhausted: Boolean
}