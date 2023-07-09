package eden.historical.sources

import com.google.gson.Gson
import eden.historical.fetching.Fetcher
import eden.historical.models.Author
import eden.historical.models.Book
import eden.historical.models.BookMetadata
import eden.historical.sources.Json.Companion.getJsonData
import org.jsoup.nodes.Document

const val goodreadsUrl = "https://www.goodreads.com"

private data class PlacesAndYears(val places: Set<String>, val years: Set<Int>)

class GoodreadsSource(private val fetcher: Fetcher) : BookSource {
    private val urlFinder = GoodreadsUrlFinder(fetcher)
    private val gson = Gson()

    override val books: Sequence<BookMetadata> = urlFinder.urls.mapNotNull { getBookFromUrl(it) }

    private fun getBookFromUrl(url: String): BookMetadata? {
        val doc = fetcher.get(url, emptyMap())
        return if (doc != null) {
            processDocument(url, doc)
        } else null
    }

    private fun processDocument(url: String, doc: Document): BookMetadata {
        val title = doc.trimmedText(".BookPageTitleSection__title h1")
        val authors = doc.select(".BookPageMetadataSection__contributor a.ContributorLink")
            .map { it.trimmedText(".ContributorLink__name") }
            .map { Author.fromFullName(it) }
        val synopsis = doc.trimmedText(".BookPageMetadataSection__description .Formatted")

        val jsonData: Json = doc.select("script#__NEXT_DATA__").first()?.getJsonData(gson)
            ?: throw Exception("Unable to find JSON metadata at $url")
        val placesAndYears = try {
            fromPlaceData(jsonData)
        } catch (e: Exception) {
            throw Exception("Error parsing place data for $title", e)
        }

        return BookMetadata(
            Book(url, title, authors),
            synopsis = synopsis.lowercase(),
            tags = getTags(jsonData),
            places = placesAndYears.places,
            years = placesAndYears.years
        )
    }

    private fun getTags(jsonData: Json): Set<String> {
        val bookData = jsonData["props"]["pageProps"]["apolloState"].getLookupWithKeyMatching(Regex("^Book:"))
        return bookData
            .list("bookGenres")
            .map { it["genre"].value("name")!! }
            .toSet()
    }

    private fun fromPlaceData(jsonData: Json): PlacesAndYears {
        val workData = jsonData["props"]["pageProps"]["apolloState"].getLookupWithKeyMatching(Regex("^Work:"))
        val rawPlaces = workData["details"].list("places")
        val places = mutableSetOf<String>()
        val years = mutableSetOf<Int>()

        for (place in rawPlaces) {
            place.value("name")?.let { places.add(it) }
            place.value("countryName")?.let { places.add(it) }
            place.int("year")?.let { years.add(it) }
        }
        return PlacesAndYears(places, years)
    }
}
