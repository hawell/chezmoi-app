package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.ui.components.createMarkerBitmap
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.VectorTileSource

@Composable
fun FactoriesLayer(
    visible: Boolean,
    sources:  Map<String, VectorTileSource>,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
) {
    if (!visible) return
    val state by mapViewModel.rplsFlow.collectAsStateWithLifecycle()
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val minZoom = 14f
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val factoryImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_factory,
            iconRes = R.drawable.factory_24px,
        ).asImageBitmap()
    }

    val factorySource = sources["installations_industrielles"]!!

    SymbolLayer(
        id = "factory-layer",
        source = factorySource,
        sourceLayer = "installations_industrielles",
        minZoom = minZoom,

        iconImage = image(factoryImage),
        iconAnchor = const(SymbolAnchor.Bottom),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

    )
}