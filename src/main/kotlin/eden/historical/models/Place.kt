package eden.historical.models

sealed class Place(val name: String) {
    class Point(name: String, val coord: LatLng) : Place(name)
    class Area(name: String, val coords: List<LatLng>) : Place(name)

    companion object {
        val default = Place.Point("Unknown", LatLng(0f, 0f))
    }
}

data class LatLng(val latitude: Float, val longitude: Float)