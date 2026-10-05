package org.chordsoft.chezmoi.ui.layers

import android.util.Log
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
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.collator
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.neq
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.expressions.dsl.textOffset
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.expressions.value.TextJustify
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapState
import org.maplibre.compose.sources.VectorTileSource

@Composable
fun CultureLayer(
    visible: Boolean,
    mapState: MapState,
    sources:  Map<String, VectorTileSource>,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
) {
    if (!visible) return
    val state by mapViewModel.rplsFlow.collectAsStateWithLifecycle()
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val images = listOf(
        Pair("center", R.drawable.history_edu_24px),
        Pair("library", R.drawable.book_ribbon_24px),
        Pair("park", R.drawable.park_24px),
        Pair("cinema", R.drawable.theater_comedy_24px),
        Pair("museum", R.drawable.wall_art_24px),
        Pair("music", R.drawable.music_note_2_24px),
        ).associate {
        it.first to remember {
            createCircleBitmap(
                context = context,
                pinRes = R.drawable.ic_circle_16,
                iconRes = it.second,
            ).asImageBitmap()
        }
    }
    val cultureTileSource = sources["culture"]!!

    SymbolLayer(
        id = "culture-symbols",
        source = cultureTileSource,
        sourceLayer = "equipements_culturels",
        minZoom = 13f,

        filter = neq(feature["type"].cast(), const("park"), collator(false)),
        iconImage = switch(
            input = feature["type"],
            case(
                label = "library",
                output = image(images["library"]!!),
            ),
            case(
                label = "cinema",
                output = image(images["cinema"]!!),
            ),
            case(
                label = "music",
                output = image(images["music"]!!),
            ),
            case(
                label = "center",
                output = image(images["center"]!!),
            ),
            case(
                label = "museum",
                output = image(images["museum"]!!),
            ),
            fallback = image(images["center"]!!),
        ),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        textField = format(span(feature["label"].cast())),
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
        onClick = {
            Log.d("ARASH", it[0].properties.toString())
            ClickResult.Consume
        }
    )
}