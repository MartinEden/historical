package eden.historical.models.countries

class GeoJSONValidationError(message: String, cause: Throwable? = null) : Exception(message, cause)

fun check(predicate: Boolean, message: String) {
    if (!predicate) {
        throw GeoJSONValidationError(message)
    }
}