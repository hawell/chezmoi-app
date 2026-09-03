package org.chordsoft.chezmoi.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

data class GeoJsonPolygon(
    val type: String,
    val coordinates: List<List<List<Double>>>
)

fun parseGeoJsonPolygon(json: String): GeoJsonPolygon {
    return Gson().fromJson(json, GeoJsonPolygon::class.java)
}

fun toLatLngList(polygon: GeoJsonPolygon): List<LatLng> {
    return polygon.coordinates[0].map { point ->
        LatLng(point[1], point[0]) // lat, lng
    }
}
