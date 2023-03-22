package eden.historical.models.countries

data class Country(val name: String, val iso3: String?, val border: List<Border>) {
    companion object {
        fun from(raw: RawFeature): Country {
            check(raw.type == "Feature", "Expected $raw to have type 'Feature'")

            check("ADMIN" in raw.properties, "Missing key 'ADMIN' in feature $raw")
            val name = raw.properties["ADMIN"]!!

            val iso3: String? = raw.properties["ISO_A3"]

            val borders = try {
                check(raw.geometry.type == "MultiPolygon", "Expected geometry to have type 'MultiPolygon'")
                raw.geometry.coordinates.map { Border.from(it) }
            } catch (e: GeoJSONValidationError) {
                throw GeoJSONValidationError("Encountered error attempting to validate GeoJSON for $name", e)
            }

            return Country(name, iso3, borders)
        }
    }
}
