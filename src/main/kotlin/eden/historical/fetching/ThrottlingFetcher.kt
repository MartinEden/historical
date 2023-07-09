package eden.historical.fetching

import org.jsoup.nodes.Document
import java.time.Duration
import java.time.Instant

class ThrottlingFetcher(private val nestedFetcher: Fetcher) : Fetcher {
    private var lastFetch = Instant.MIN

    override fun get(url: String, cookies: Map<String, String>): Document? {
        throttle()
        return nestedFetcher.get(url, cookies)
    }

    override val exhausted
        get() = nestedFetcher.exhausted

    private fun throttle() {
        val now = Instant.now()
        val timeSinceLastFetch = Duration.between(lastFetch, now)
        val timeToWait = Duration.ofSeconds(5) - timeSinceLastFetch
        if (timeToWait > Duration.ZERO) {
            val msToWait = timeToWait.toMillis()
            println("Sleeping for $msToWait")
            Thread.sleep(msToWait)
        }
        lastFetch = now
    }
}