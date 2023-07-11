package eden.historical.sources

import com.google.gson.Gson
import eden.historical.fetching.Fetcher
import eden.historical.models.Author
import eden.historical.models.Book
import eden.historical.models.BookMetadata
import eden.historical.sources.Json.Companion.getJsonData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist

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
        val apolloState = jsonData["props"]["pageProps"]["apolloState"]

        val placesAndYears = try {
            fromPlaceData(apolloState)
        } catch (e: Exception) {
            throw Exception("Error parsing place data for $title", e)
        }

        return BookMetadata(
            Book(url, title, authors),
            synopsis = cleanText(synopsis),
            reviews = getReviews(apolloState).toList(),
            tags = getTags(apolloState),
            places = placesAndYears.places,
            years = placesAndYears.years
        )
    }

    private fun getTags(jsonData: Json): Set<String> {
        val bookData = jsonData.getLookupWithKeyMatching(Regex("^Book:"))
        return bookData
            .list("bookGenres")
            .map { it["genre"].value("name")!! }
            .toSet()
    }

    private fun fromPlaceData(jsonData: Json): PlacesAndYears {
        val workData = jsonData.getLookupWithKeyMatching(Regex("^Work:"))
        val rawPlaces = workData["details"].list("places")
        val places = mutableSetOf<String>()
        val years = mutableSetOf<Int>()

        for (place in rawPlaces) {
            place.value("name")?.let { rawPlace ->
                // Sometimes goodreads data is messy and instead of being name: Licolnshire, countryName: England
                // we instead get name: Licolnshire, England, countryName: null
                // So split on any commas to get all the parts separately
                for (part in rawPlace.split(',').map { it.trim() }) {
                    places.add(part)
                }
            }
            place.value("countryName")?.let { places.add(it) }
            place.int("year")?.let { years.add(it) }
        }
        return PlacesAndYears(places, years)
    }

    private fun getReviews(jsonData: Json): Sequence<String> {
        val reviewLookups = jsonData.getLookupsWithKeyMatching(Regex("^Review:"))
        return reviewLookups.mapNotNull { lookup ->
            lookup.value("text")?.let {
                cleanText(it)
            }
        }
    }

    private fun cleanText(text: String) = Jsoup.clean(text, Safelist.none()).lowercase().replace('’', '\'')
}
