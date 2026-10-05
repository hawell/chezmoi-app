package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.VectorTileSource
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty

@Composable
fun PollutionLayer(
    visible: Boolean,
    sources:  Map<String, VectorTileSource>,
    mapViewModel: MapViewModel,
    onMarkerClick: () -> Unit
) {
    if (!visible) return
    val source = sources["polluted_sites"]!!
    LineLayer(
        id = "polluted-sites-lines",
        source = source,
        sourceLayer = "polluted_sites",
        width = const(1.dp),
        color = const(Color.Gray),
        minZoom = 11f,
        //maxZoom = 16f
    )

    FillLayer(
        id = "polluted-sites-fill",
        source = source,
        sourceLayer = "polluted_sites",
        minZoom = 11f,
        opacity = const(0.5f),
        color = const(Color.Yellow),
        onClick = { features ->
            val value = listOf(
                "Nom: ${features[0].properties?.get("nom_instr")}",
                "État: ${features[0].properties?.get("stat_instr")}",
                "Description: ${features[0].properties?.get("descript")}",
            )
            mapViewModel.updateMarkerInfo(MapViewModel.MarkerInfo(
                type = MapViewModel.MarkerType.PollutedSite,
                items = listOf(
                    MapViewModel.MarkerInfoItem(
                        id = features[0].getStringProperty("id")?: "",
                        data = value
                    )
                )
            ))
            onMarkerClick()
            ClickResult.Consume
        }
    )
}