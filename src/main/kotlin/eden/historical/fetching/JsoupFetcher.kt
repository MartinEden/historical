package eden.historical.fetching

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupFetcher : Fetcher {
    override fun get(url: String, cookies: Map<String, String>): Document =
        Jsoup
            .connect(url)
            .cookies(cookies)
            .get()

    //fun post(url: String, data: Map<String, String>): Document = Jsoup.connect(url).data(data).post()

    override val exhausted: Boolean = false
}
