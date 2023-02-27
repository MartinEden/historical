package eden.historical.fetching

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Thread.sleep
import java.time.Duration
import java.time.Instant
import kotlin.io.path.Path

class CachingFetcher : Fetcher {
    private val cacheRoot = Path(System.getProperty("user.home"), ".eden.historical", "cache")
    private val invalidFileCharacters = Regex("""[^a-zA-Z0-9]+""")
    private var lastFetch = Instant.MIN

    init {
        val cacheDir = cacheRoot.toFile()
        if (!cacheDir.isDirectory) {
            println("Creating $cacheDir")
            cacheDir.mkdirs()
        }
    }

    override fun get(url: String): Document {
        val mangledUrl = url.replace(invalidFileCharacters, "_") + ".html"
        val cacheFile = cacheRoot.resolve(mangledUrl).toFile()
        return if (cacheFile.isFile) {
            Jsoup.parse(cacheFile)
        } else {
            println("Fetching $url")
            throttle()
            val document = Jsoup.connect(url).get()
            val text = document.html()
            cacheFile.writeText(text)
            document
        }
    }

    private fun throttle() {
        val now = Instant.now()
        val timeSinceLastFetch = Duration.between(lastFetch, now)
        val timeToWait = Duration.ofSeconds(5) - timeSinceLastFetch
        if (timeToWait > Duration.ZERO) {
            val msToWait = timeToWait.toMillis()
            println("Sleeping for $msToWait")
            sleep(msToWait)
        }
        lastFetch = now
    }
}