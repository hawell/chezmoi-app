package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.expressions.value.TextJustify
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.rememberVectorTileSource

@Composable
fun PermisLayer(
    visible: Boolean,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
) {
    if (!visible) return
    val state by mapViewModel.rplsFlow.collectAsStateWithLifecycle()
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val permisAmenagerSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/permis_amenager/{z}/{x}/{y}.pbf",
        )
    )
    val permisDemolirSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/permis_demolir/{z}/{x}/{y}.pbf",
        )
    )
    val permisConstruireNonResidentielsSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/permis_construire_non_residentiels/{z}/{x}/{y}.pbf",
        )
    )
    val PermisConstruireResidentielsSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/permis_construire_residentiels/{z}/{x}/{y}.pbf",
        )
    )

    SymbolLayer(
        id = "permis-amenager-symbols",
        sourceLayer = "permis_amenager_loc",
        source = permisAmenagerSource,
        minZoom = 10f,

        iconImage = image(painterResource(R.drawable.ic_cluster_circle)),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = const("AM").cast(),
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
    )

    SymbolLayer(
        id = "permis-demolir-symbols",
        source = permisDemolirSource,
        sourceLayer = "permis_demolir_loc",
        minZoom = 10f,

        iconImage = image(painterResource(R.drawable.ic_cluster_circle)),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = const("DM").cast(),
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
    )

    SymbolLayer(
        id = "permis-construire-non-residentiels-symbols",
        source = permisConstruireNonResidentielsSource,
        sourceLayer = "permis_construire_non_residentiels_loc",
        minZoom = 10f,

        iconImage = image(painterResource(R.drawable.ic_cluster_circle)),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = const("CN").cast(),
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
    )

    SymbolLayer(
        id = "permis-logement-symbols",
        source = PermisConstruireResidentielsSource,
        sourceLayer = "permis_construire_residentiels_loc",
        minZoom = 10f,

        iconImage = image(painterResource(R.drawable.ic_cluster_circle)),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = const("CR").cast(),
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
    )
}