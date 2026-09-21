package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.rememberVectorTileSource

@Composable
fun AdminLayer(visible: Boolean) {
    if (!visible) return
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

    LineLayer(
        id = "admin-border-regions",
        source = adminRegionsSource,
        sourceLayer = "regions",
        width = const(2.dp),
        color = const(Color.Red),
        minZoom = 5f,
        maxZoom = 8f
    )

    LineLayer(
        id = "admin-border-departements",
        source = adminDepartementsSource,
        sourceLayer = "departements",
        width = const(2.dp),
        color = const(Color.Red),
        minZoom = 8f,
        maxZoom = 13f
    )

    LineLayer(
        id = "admin-border-arrondissements",
        source = adminArrondissementsSource,
        sourceLayer = "arrondissements",
        width = const(2.dp),
        color = const(Color.Red),
        minZoom = 11f,
        maxZoom = 16f
    )

    LineLayer(
        id = "admin-border-communes",
        source = adminCommuesSource,
        sourceLayer = "communes",
        width = const(2.dp),
        color = const(Color.Red),
        minZoom = 15f,
        maxZoom = 24f
    )
}