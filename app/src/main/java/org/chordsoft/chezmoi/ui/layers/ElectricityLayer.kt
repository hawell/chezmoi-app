package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.chordsoft.chezmoi.ui.theme.Orange1
import org.chordsoft.chezmoi.ui.theme.Orange2
import org.chordsoft.chezmoi.ui.theme.Red1
import org.chordsoft.chezmoi.ui.theme.Red2
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.rememberVectorTileSource

@Composable
fun ElectricityLayer(
    visible: Boolean,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
) {
    if (!visible) return
    val source = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/electricity/{z}/{x}/{y}.pbf",
        )
    )

    LineLayer(
        id = "electricity-line-m-aerial",
        source = source,
        sourceLayer = "reseau_aerien_moyenne_tension_hta",
        minZoom = 5f,
        color = const(Orange1),
        width = const(2.dp)
    )

    LineLayer(
        id = "electricity-line-m-souterrain",
        source = source,
        sourceLayer = "reseau_souterrain_moyenne_tension_hta",
        minZoom = 5f,
        color = const(Orange2),
        width = const(2.dp),
        dasharray = const(listOf(2, 1))
    )

    LineLayer(
        id = "electricity-line-h-aerial",
        source = source,
        sourceLayer = "lignes_electriques_aerien_haute_tension",
        minZoom = 5f,
        color = const(Red1),
        width = const(2.dp)
    )

    LineLayer(
        id = "electricity-line-h-souterrain",
        source = source,
        sourceLayer = "lignes_electriques_souterrain_haute_tension",
        minZoom = 5f,
        color = const(Red2),
        width = const(2.dp),
        dasharray = const(listOf(2, 1))
    )

}
