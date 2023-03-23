package eden.historical.fetching

import eden.historical.Settings
import org.jsoup.nodes.Document

class RequiresLoginFetcher(private val nestedFetcher: JsoupFetcher): Fetcher {
    private var loginCookies: Map<String, String?> = mapOf(
        "at-main" to Settings.atMain,
        "ubid-main" to Settings.ubidMain
    )
    override val exhausted
        get() = nestedFetcher.exhausted

    override fun get(url: String, cookies: Map<String, String>): Document {
        if (loginCookies["at-main"].isNullOrBlank() || loginCookies["ubid-main"].isNullOrBlank()) {
            getCookiesFromUser()
        }
        // TODO: Check cookies haven't expired
        return nestedFetcher.get(url, cookies + loginCookies.mapValues { (_, v) -> v!! })
    }

    private fun getCookiesFromUser() {
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