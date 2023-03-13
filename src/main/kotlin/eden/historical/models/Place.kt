package eden.historical.models

sealed class Place(val name: String) {
    class Point(name: String, val coord: LatLng) : Place(name)
    class Area(name: String, val coords: List<LatLng>) : Place(name)
    object Unknown : Place("Unknown")

    override fun toString() = name
}

data class LatLng(val latitude: Float, val longitude: Float)