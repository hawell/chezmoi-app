package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.chordsoft.chezmoi.ui.theme.DarkYellow
import org.chordsoft.chezmoi.ui.theme.Orange1
import org.chordsoft.chezmoi.ui.theme.Orange2
import org.chordsoft.chezmoi.ui.theme.Red1
import org.chordsoft.chezmoi.ui.theme.Red2
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.VectorTileSource

@Composable
fun GasLayer(
    visible: Boolean,
    sources:  Map<String, VectorTileSource>,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
) {
    if (!visible) return
    val source = sources["gas"]!!

    LineLayer(
        id = "gas-pipe",
        source = source,
        sourceLayer = "gas",
        minZoom = 14f,
        color = const(DarkYellow),
        width = const(2.dp)
    )
}
