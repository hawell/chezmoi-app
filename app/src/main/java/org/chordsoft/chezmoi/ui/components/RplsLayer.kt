package org.chordsoft.chezmoi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.data.sources.SourceState
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.expressions.dsl.*
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.expressions.value.TextJustify
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Geometry
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

@Composable
fun RplsLayer(
    flow: StateFlow<SourceState<String>>,
    camera: CameraState,
    onClick: (id: Int, type: MapViewModel.MarkerType) -> Unit
) {
    val state by flow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val source = rememberGeoJsonSource(
        data = GeoJsonData.Features(FeatureCollection<Geometry, JsonObject?>(emptyList()))
    )
    if (state is SourceState.Success<String>) {
        source.setData(GeoJsonData.JsonString((state as SourceState.Success<String>).data))
    }
    SymbolLayer(
        id = "rpls-symbols",
        source = source,

        iconImage = image(painterResource(R.drawable.ic_cluster_circle)),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = format(span(feature["count"].cast())),
        textFont = const(
            listOf("Open Sans Regular", "Arial Unicode MS Regular")
        ),
        textColor = const(Color.White),
        textSize = const(10.sp),
        textJustify = const(TextJustify.Center),
        textAnchor = const(SymbolAnchor.Center),
        textAllowOverlap = const(true),
        textIgnorePlacement = const(true),
        textOptional = const(true),
        onClick = { features ->
            val count = features[0].properties?.get("count").toString().toInt()
            if (count > 1) {
                if (features[0].geometry is Point) {
                    val point = features[0].geometry as Point
                    val position = Position(
                        latitude = point.coordinates.latitude,
                        longitude = point.coordinates.longitude
                    )
                    scope.launch {
                        camera.animateTo(
                            CameraPosition(target = position, zoom = camera.position.zoom + 2)
                        )
                    }
                }
            } else {
              onClick(features[0].id?.int ?: 0, MapViewModel.MarkerType.RplsMarker)
            }
            ClickResult.Consume
        }
    )
}