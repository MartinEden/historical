package eden.historical.models.countries

import com.google.gson.Gson
import java.io.File

class CountryDataSource(private val path: String = defaultPath!!) {
    fun load(): List<Country> {
        val gson = Gson()
        val rawData = File(path).reader().use {
            gson.fromJson(it, RawCountryData::class.java)
        }
        return from(rawData)
    }

    private fun from(raw: RawCountryData): List<Country> {
        check(
            raw.type == "FeatureCollection",
            "Expected top level object to have type 'FeatureCollection'"
        )
        return raw.features.map { Country.from(it) }
    }

    companion object {
        val defaultPath = Thread.currentThread().contextClassLoader?.getResource("countries.geojson")?.path
    }
}