package eden.historical.models.countries

import eden.historical.models.LatLng

data class Border(val envelope: Polygon, val holes: List<Polygon>) {
    companion object {
        fun from(raw: List<List<List<Float>>>): Border {
            val envelope = Polygon.from(raw.first())
            val holes = raw.drop(1).map { Polygon.from(it) }
            return Border(envelope, holes)
        }
    }
}

data class Polygon(val coordinates: List<LatLng>) {
    companion object {
        fun from(raw: List<List<Float>>) = Polygon(raw.map { toLatLng(it) })

        private fun toLatLng(coordinates: List<Float>): LatLng {
            check(
                coordinates.size == 2,
                "LatLng had ${coordinates.size} components, instead of 2"
            )
            return LatLng(coordinates[0], coordinates[1])
        }
    }
}