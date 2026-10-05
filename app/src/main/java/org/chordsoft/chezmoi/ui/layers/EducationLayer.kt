package org.chordsoft.chezmoi.ui.layers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.ui.components.createCircleBitmap
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.textOffset
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.expressions.value.TextJustify
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapState
import org.maplibre.compose.sources.VectorTileSource
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty
import kotlin.collections.listOf

@Composable
fun EducationLayer(
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
    val schoolImage = remember {
        createCircleBitmap(
            context = context,
            pinRes = R.drawable.ic_circle_16,
            iconRes = R.drawable.school_24px,
        ).asImageBitmap()
    }
    val educationTileSource = sources["education"]!!

    SymbolLayer(
        id = "education-symbols",
        source = educationTileSource,
        sourceLayer = "education",
        minZoom = 13f,

        iconImage = image(schoolImage),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = format(span(feature["name"].cast())),
        textFont = const(
            listOf(style.textFont)
        ),
        textColor = const(Color.Black),
        textSize = const(10.sp),
        textJustify = const(TextJustify.Center),
        textAnchor = const(SymbolAnchor.Top),
        textOffset = textOffset(0.em, 1.5.em),
        textAllowOverlap = const(false),
        textIgnorePlacement = const(false),
        textOptional = const(true),
        textOpacity = const(1f),
        onClick = { features ->
            val value = listOf(
                "Nom: ${features[0].properties?.get("name")}",
                "Denomination: ${features[0].properties?.get("denomination")}",
                "Secteur: ${features[0].properties?.get("sector")}",
            )
            mapViewModel.updateMarkerInfo(MapViewModel.MarkerInfo(
                type = MapViewModel.MarkerType.Education,
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