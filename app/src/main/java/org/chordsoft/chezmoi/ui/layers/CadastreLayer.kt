package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.rememberVectorTileSource
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty

@Composable
fun CadastreLayer(
    visible: Boolean,
    mapViewModel: MapViewModel,
    onMarkerClick: () -> Unit
) {
    if (!visible) return
    val source = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/cadastre/{z}/{x}/{y}.pbf"
        )
    )

    LineLayer(
        id = "cadastre-batiments-line",
        source = source,
        sourceLayer = "batiments",
        width = const(1.dp),
        color = const(Color.Gray),
        minZoom = 14f,
        //maxZoom = 16f
    )

    LineLayer(
        id = "cadastre-parcelles-line",
        source = source,
        sourceLayer = "parcelles",
        width = const(1.dp),
        color = const(Color.Gray),
        minZoom = 13f,
        //maxZoom = 16f
    )

    FillLayer(
        id = "cadastre-parcelles-fill",
        source = source,
        sourceLayer = "parcelles",
        minZoom = 13f,
        opacity = const(0f),
        onClick = { features ->
            val value = listOf(
                "contenance: ${features[0].properties?.get("contenance")}",
                "id: ${features[0].properties?.get("id")}",
                "commune: ${features[0].properties?.get("commune")}",
                "prefix: ${features[0].properties?.get("prefix")}",
                "section: ${features[0].properties?.get("section")}",
                "numero: ${features[0].properties?.get("numero")}",
                "updated: ${features[0].properties?.get("updated")}",
                "created: ${features[0].properties?.get("created")}",
            )
            mapViewModel.updateMarkerInfo(MapViewModel.MarkerType.CadastreParcelle, features[0].getStringProperty("id")?: "", value)
            onMarkerClick()
            ClickResult.Consume
        }
    )

    LineLayer(
        id = "cadastre-sections-line",
        source = source,
        sourceLayer = "sections",
        width = const(1.dp),
        color = const(Color.Gray),
        minZoom = 11f,
        //maxZoom = 16f,
    )
}