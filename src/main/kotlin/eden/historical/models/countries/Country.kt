package eden.historical.models.countries

data class Country(val names: List<String>, val iso3: String?, val border: List<Border>) {
    val defaultName by lazy { names.first() }

    companion object {
        fun from(raw: RawFeature): Country {
            check(raw.type == "Feature", "Expected $raw to have type 'Feature'")

            check("names" in raw.properties, "Missing key 'names' in feature $raw")
            @Suppress("UNCHECKED_CAST")
            val names = raw.properties["names"] as List<String>

            val iso3 = raw.properties["ISO_A3"] as String?

            val borders = try {
                check(raw.geometry.type == "MultiPolygon", "Expected geometry to have type 'MultiPolygon'")
                raw.geometry.coordinates.map { Border.from(it) }
            } catch (e: GeoJSONValidationError) {
                throw GeoJSONValidationError("Encountered error attempting to validate GeoJSON for ${names.first()}", e)
            }

            return Country(names, iso3, borders)
        }
    }
}
