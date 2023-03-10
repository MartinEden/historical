package eden.historical.fetching

import org.jsoup.nodes.Document

class RequiresLoginFetcher(private val nestedFetcher: JsoupFetcher): Fetcher {
    private var loginCookies: Map<String, String> = emptyMap()
    override val exhausted
        get() = nestedFetcher.exhausted

    override fun get(url: String, cookies: Map<String, String>): Document {
        if (loginCookies.isEmpty()) {
            doLogin()
        }
        return nestedFetcher.get(url, cookies + loginCookies)
    }

    private fun doLogin() {
        println("Missing Goodreads cookie data.")
        loginCookies = mapOf(
            "at-main" to promptUser("Please provide value for 'at-main' cookie: "),
            "ubid-main" to promptUser("Please provide value for 'ubid-main' cookie: ")
        )
    }

    private fun promptUser(prompt: String): String {
        var value: String? = null
        while (value == null) {
            print(prompt)
            value = readlnOrNull()
        }
        return value
    }
}