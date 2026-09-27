package org.chordsoft.chezmoi.ui.layers

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MapState
import org.maplibre.compose.sources.rememberVectorTileSource
import org.maplibre.spatialk.geojson.Polygon
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AdminLayer(
    visible: Boolean,
    mapViewModel: MapViewModel,
    mapState: MapState,
    onIrisClick: () -> Unit
) {
    if (!visible) return
    val scope = rememberCoroutineScope()
    val adminPublicSource = rememberVectorTileSource(
        tiles = listOf(
            "https://openmaptiles.data.gouv.fr/data/decoupage-administratif/{z}/{x}/{y}.pbf"
        )
    )
    val adminRegionsSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/regions_admin/{z}/{x}/{y}.pbf",
        )
    )
    val adminDepartementsSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/departements_admin/{z}/{x}/{y}.pbf",
        )
    )
    val adminArrondissementsSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/arrondissements_admin/{z}/{x}/{y}.pbf",
        )
    )
    val adminCommuesSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/communes_admin/{z}/{x}/{y}.pbf",
        )
    )

    val irisSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/iris/{z}/{x}/{y}.pbf",
        )
    )

    LineLayer(
        id = "admin-border-regions",
        //source = adminRegionsSource,
        source = adminPublicSource,
        sourceLayer = "regions",
        width = const(2.dp),
        color = const(Color.Black),
        minZoom = 4f,
        maxZoom = 6f
    )
    FillLayer(
        id = "admin-fill-regions",
        source = adminPublicSource,
        sourceLayer = "regions",
        minZoom = 4f,
        maxZoom = 6f,
        opacity = const(0f),
        onClick = { features ->
            val polygon = (features[0].geometry as? Polygon) ?: return@FillLayer ClickResult.Pass
            val coordinates = polygon.coordinates.firstOrNull() ?: return@FillLayer ClickResult.Pass

            scope.launch {
                mapState.animateCameraPosition(CameraPosition(target = center(coordinates), zoom = mapState.cameraPosition.zoom + 2.0), duration = 500.milliseconds)
            }
            ClickResult.Consume
        }
    )

    LineLayer(
        id = "admin-border-departements",
        //source = adminDepartementsSource,
        source = adminPublicSource,
        sourceLayer = "departements",
        width = const(2.dp),
        color = const(Color.Black),
        minZoom = 6f,
        maxZoom = 10f
    )
    FillLayer(
        id = "admin-fill-departements",
        source = adminPublicSource,
        sourceLayer = "departements",
        minZoom = 6f,
        maxZoom = 10f,
        opacity = const(0f),
        onClick = { features ->
            val polygon = (features[0].geometry as? Polygon) ?: return@FillLayer ClickResult.Pass
            val coordinates = polygon.coordinates.firstOrNull() ?: return@FillLayer ClickResult.Pass

            scope.launch {
                mapState.animateCameraPosition(CameraPosition(target = center(coordinates), zoom = mapState.cameraPosition.zoom + 2.0), duration = 500.milliseconds)
            }
            ClickResult.Consume
        }
    )

    LineLayer(
        id = "admin-border-communes",
        //source = adminCommuesSource,
        source = adminPublicSource,
        sourceLayer = "communes",
        width = const(2.dp),
        color = const(Color.Black),
        minZoom = 10f,
        maxZoom = 12f
    )
    FillLayer(
        id = "admin-fill-communes",
        source = adminPublicSource,
        sourceLayer = "communes",
        minZoom =10f,
        maxZoom = 12f,
        opacity = const(0f),
        onClick = { features ->
            val polygon = (features[0].geometry as? Polygon) ?: return@FillLayer ClickResult.Pass
            val coordinates = polygon.coordinates.firstOrNull() ?: return@FillLayer ClickResult.Pass

            scope.launch {
                mapState.animateCameraPosition(CameraPosition(target = center(coordinates), zoom = mapState.cameraPosition.zoom + 2.0), duration = 500.milliseconds)
            }
            ClickResult.Consume
        }
    )

/*
    LineLayer(
        id = "admin-border-mairies",
        //source = adminCommuesSource,
        source = adminPublicSource,
        sourceLayer = "mairies",
        width = const(3.dp),
        color = const(Color.Yellow),
        //minZoom = 12f,
        //maxZoom = 16f
    )
*/

    LineLayer(
        id = "admin-border-iris",
        source = irisSource,
        sourceLayer = "iris",
        width = const(2.dp),
        color = const(Color.Black),
        minZoom = 12f,
    )
    FillLayer(
        id = "admin-fill-iris",
        source = irisSource,
        sourceLayer = "iris",
        minZoom =12f,
        opacity = const(0f),
        onClick = { features ->
            Log.d("ARASH", features[0].properties.toString())
            val irisInfo = MapViewModel.IrisInfo(
                id = features[0].id.toString(),
                name = features[0].properties?.get("nom_officiel")?.jsonPrimitive?.content ?: "-",
                commune = features[0].properties?.get("nom_commune")?.jsonPrimitive?.content ?: "-"
            )
            mapViewModel.updateIrisInfo(irisInfo)
            onIrisClick()
            ClickResult.Consume
        }
    )

}

private fun center(coordinates: List<Position>): Position {
    val minLon = coordinates.minOf { it.longitude }
    val maxLon = coordinates.maxOf { it.longitude }
    val minLat = coordinates.minOf { it.latitude }
    val maxLat = coordinates.maxOf { it.latitude }

    return Position(longitude = (minLon+maxLon)/2.0, latitude = (minLat+maxLat)/2.0)
}
