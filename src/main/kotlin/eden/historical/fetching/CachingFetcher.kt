package eden.historical.fetching

import eden.historical.Settings
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import kotlin.io.path.Path

class CachingFetcher(private val nestedFetcher: Fetcher) : Fetcher {
    private val cacheRoot = Path(System.getProperty("user.home"), ".eden.historical", "cache")
    private val invalidFileCharacters = Regex("""[^a-zA-Z0-9]+""")
    private var cacheMissCount = 0

    override val exhausted
        get() = cacheMissCount >= Settings.maximumCacheMisses || nestedFetcher.exhausted

    init {
        val cacheDir = cacheRoot.toFile()
        if (!cacheDir.isDirectory) {
            println("Creating $cacheDir")
            cacheDir.mkdirs()
        }
    }

    override fun get(url: String, cookies: Map<String, String>) : Document? {
        val cacheFile = getCacheFile(url)
        return if (cacheFile.isFile) {
            Jsoup.parse(cacheFile)
        } else {
            updateCacheMissCount()
            if (exhausted) {
                null
            } else {
                println("Fetching $url")
                val document = nestedFetcher.get(url, cookies)!!
                saveToCache(document, cacheFile)
                document
            }
        }
    }

    private fun getCacheFile(url: String): File {
        val mangledUrl = url.replace(invalidFileCharacters, "_") + ".html"
        return cacheRoot.resolve(mangledUrl).toFile()
    }

    private fun saveToCache(document: Document, cacheFile: File) {
        val text = document.html()
        cacheFile.writeText(text)
    }

    private fun updateCacheMissCount() {
        cacheMissCount++
        if (exhausted) {
            println("Fetcher exhausted after $cacheMissCount cache misses")
        }
    }
}