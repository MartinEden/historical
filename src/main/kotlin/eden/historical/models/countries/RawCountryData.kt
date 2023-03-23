package eden.historical.models.countries

data class RawCountryData(val type: String, val features: List<RawFeature>)
data class RawFeature(val type: String, val properties: Map<String, Any>, val geometry: RawGeometry)
data class RawGeometry(val type: String, val coordinates: List<List<List<List<Float>>>>)

