package org.chordsoft.chezmoi.ui.layers

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.ui.components.createMarkerBitmap
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.expressions.value.TextJustify
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapState
import org.maplibre.compose.sources.VectorTileSource
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

@Composable
fun RplsLayer(
    visible: Boolean,
    mapState: MapState,
    sources:  Map<String, VectorTileSource>,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    onMarkerClick: () -> Unit
) {
    if (!visible) return
    val state by mapViewModel.rplsFlow.collectAsStateWithLifecycle()
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val rplsImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_rpls,
            iconRes = R.drawable.shield_with_house_24px,
        ).asImageBitmap()
    }
    val rplsTileSource = sources["rpls"]!!

    SymbolLayer(
        id = "rpls-symbols",
        source = rplsTileSource,
        sourceLayer = "rpls",
        minZoom = 10f,

        iconImage = switch(
            input = feature["count"],
            case(
                label = 1,
                output = image(rplsImage),
            ),
            fallback = image(painterResource(R.drawable.ic_cluster_circle)),
        ),
        iconAnchor = switch(
            input = feature["count"],
            case(
                label = 1,
                output = const(SymbolAnchor.Bottom)
            ),
            fallback = const(SymbolAnchor.Center)
        ),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = format(span(feature["count"].cast())),
        textFont = const(
            listOf(style.textFont)
        ),
        textColor = const(Color.White),
        textSize = const(10.sp),
        textJustify = const(TextJustify.Center),
        textAnchor = const(SymbolAnchor.Center),
        textAllowOverlap = const(true),
        textIgnorePlacement = const(true),
        textOptional = const(true),
        textOpacity = switch(
            input = feature["count"],
            case(
                label = 1,
                output = const(0f)
            ),
            fallback = const(1f)
        ),
        onClick = { features ->
            val count = features[0].properties?.get("count").toString().toIntOrNull() ?: 1
            Log.d("ARASH", features[0].properties.toString())
            if (count > 1) {
                if (features[0].geometry is Point) {
                    val point = features[0].geometry as Point
                    scope.launch {
                        mapState.animateCameraPosition(
                            CameraPosition(
                                target = Position(latitude = point.coordinates.latitude, longitude = point.coordinates.longitude),
                                zoom = mapState.cameraPosition.zoom + 2f
                            )
                        )
                    }
                }
            } else {
                scope.launch {
                    val id = features[0].properties?.get("id")?.toString()?.toIntOrNull() ?: 0
                    val details = mapViewModel.getRplsDetails(id)
                    details?.let {
                        mapViewModel.updateMarkerInfo(
                            MapViewModel.MarkerInfo(
                                type = MapViewModel.MarkerType.RplsMarker,
                                items = listOf(
                                    MapViewModel.MarkerInfoItem(
                                        id = id.toString(),
                                        data = listOf(
                                            "${details.number} ${details.address}",
                                            "${details.postalCode} ${details.city}",
                                            "Année construction: ${details.constructionYear}",
                                            "Nbr PLAI: ${details.numPlai}",
                                            "Nbr PLUS: ${details.numPlus}",
                                            "Nbr PLS: ${details.numPls}",
                                            "Nbr PLI: ${details.numPli}",
                                            "Nbr Inconnu: ${details.numUnknown}",
                                        )
                                    )
                                ),
                            )
                        )
                        onMarkerClick()
                    }
                }
            }
            ClickResult.Consume
        }
    )
}