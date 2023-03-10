package eden.historical.sources

import com.google.gson.Gson
import eden.historical.fetching.Fetcher
import eden.historical.models.Author
import eden.historical.models.Book
import eden.historical.models.BookMetadata
import eden.historical.sources.Json.Companion.getJsonData
import org.jsoup.nodes.Document

const val goodreadsUrl = "https://www.goodreads.com"

class GoodreadsSource(private val fetcher: Fetcher) : BookSource {
    private val urlFinder = GoodreadsUrlFinder(fetcher)
    private val gson = Gson()

    override val books: Sequence<BookMetadata> = urlFinder.urls.map { getBookFromUrl(it) }

    private fun getBookFromUrl(url: String): BookMetadata {
        val doc = fetcher.get(url, emptyMap())

        val title = doc.trimmedText(".BookPageTitleSection__title h1")
        val authors = doc.select(".BookPageMetadataSection__contributor a.ContributorLink")
            .map { it.trimmedText(".ContributorLink__name") }
            .map { Author.fromFullName(it) }
        val synopsis = doc.trimmedText(".BookPageMetadataSection__description .Formatted")

        val jsonData: Json = doc.select("script#__NEXT_DATA__").first()?.getJsonData(gson)
            ?: throw Exception("Unable to find JSON metadata at $url")

        return BookMetadata(
            Book(url, title, authors),
            synopsis = synopsis.lowercase(),
            tags = getTags(jsonData),
            places = getPlaces(jsonData)
        )
    }

    private fun getTags(jsonData: Json): Set<String> {
        val bookData = jsonData["props"]["pageProps"]["apolloState"].getLookupWithKeyMatching(Regex("^Book:"))
        return bookData
            .list("bookGenres")
            .map { it["genre"].value("name") }
            .toSet()
    }

    private fun getPlaces(jsonData: Json): Set<String> {
        val workData = jsonData["props"]["pageProps"]["apolloState"].getLookupWithKeyMatching(Regex("^Work:"))
        return workData["details"]
            .list("places")
            .map { it.value("name") }
            .toSet()
    }
}