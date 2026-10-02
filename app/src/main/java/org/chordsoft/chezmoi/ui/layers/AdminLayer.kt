package org.chordsoft.chezmoi.ui.layers

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import org.chordsoft.chezmoi.data.sources.Statistics
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.FeaturesClickHandler
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MapState
import org.maplibre.compose.sources.VectorTileSource
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AdminLayer(
    mapViewModel: MapViewModel,
    sources:  Map<String, VectorTileSource>,
    settingsViewModel: SettingsViewModel,
    mapState: MapState,
    onIrisClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val layers by settingsViewModel.layers.collectAsStateWithLifecycle()
    val statistics by settingsViewModel.statistics.collectAsStateWithLifecycle()
    if (!layers.limiteAdministrative && statistics == Statistics.None) return

    val defaultOnClick: FeaturesClickHandler = { features ->
        Log.d("ARASH", features[0].properties.toString())

        val centerLat = ((features[0].properties?.get("center_lat") ?: 0) as JsonPrimitive).content.toDouble()
        val centerLong = ((features[0].properties?.get("center_long") ?: 0) as JsonPrimitive).content.toDouble()
        scope.launch {
            mapState.animateCameraPosition(
                CameraPosition(
                    target = Position(longitude = centerLong, latitude = centerLat),
                    zoom = mapState.cameraPosition.zoom + 2.0
                ), duration = 500.milliseconds
            )
        }
        ClickResult.Consume
    }

    BorderLayer(
        id = "admin-regions",
        borders = layers.limiteAdministrative,
        source = sources["regions_admin"]!!,
        sourceLayer = "regions",
        minZoom = 4f,
        maxZoom = 6f,
        statistics = statistics,
        onClick = defaultOnClick
    )

    BorderLayer(
        id = "admin-departements",
        borders = layers.limiteAdministrative,
        source = sources["departements_admin"]!!,
        sourceLayer = "departements",
        minZoom = 6f,
        maxZoom = 10f,
        statistics = statistics,
        onClick = defaultOnClick
    )

    BorderLayer(
        id = "admin-communes",
        borders = layers.limiteAdministrative,
        source = sources["communes_admin"]!!,
        sourceLayer = "communes",
        minZoom = 10f,
        maxZoom = 15f,
        statistics = statistics,
        onClick = defaultOnClick
    )

    BorderLayer(
        id = "admin-arrondissements-municipal",
        borders = layers.limiteAdministrative,
        source = sources["arrondissements_municipal_admin"]!!,
        sourceLayer = "arrondissements_municipal",
        minZoom = 10f,
        maxZoom = 13f,
        statistics = statistics,
        onClick = defaultOnClick
    )

    BorderLayer(
        id = "admin-iris",
        borders = layers.limiteAdministrative,
        source = sources["iris"]!!,
        sourceLayer = "iris",
        minZoom = 13f,
        maxZoom = 16f,
        statistics = statistics,
        onClick = { features ->
            Log.d("ARASH", features[0].properties.toString())
            val irisInfo = MapViewModel.IrisInfo(
                id = features[0].id.toString(),
                name = features[0].properties?.get("nom_officiel")?.jsonPrimitive?.content ?: "-",
                commune = features[0].properties?.get("nom_commune")?.jsonPrimitive?.content ?: "-"
            )
            mapViewModel.updateIrisInfo(irisInfo)
            onIrisClick()
            ClickResult.Consume
        }
    )
}


@Composable
fun BorderLayer(
    id: String,
    borders: Boolean,
    source: VectorTileSource,
    sourceLayer: String,
    minZoom: Float,
    maxZoom: Float,
    statistics: Statistics,
    onClick: FeaturesClickHandler? = null,
) {
    when (statistics) {
        Statistics.None -> FillLayer(
            id = "$id-fill",
            source = source,
            sourceLayer = sourceLayer,
            minZoom = minZoom,
            maxZoom = maxZoom,
            opacity = const(0f),
            onClick = onClick
        )
        Statistics.Poverty -> ScoreLayer(
            id = "$id-fill",
            source = source,
            sourceLayer = sourceLayer,
            featureName = "poverty_score",
            minZoom = minZoom,
            maxZoom = maxZoom,
            onClick = onClick
        )
        Statistics.Population -> ScoreLayer(
            id = "$id-fill",
            source = source,
            sourceLayer = sourceLayer,
            featureName = "population_score",
            minZoom = minZoom,
            maxZoom = maxZoom,
            onClick = onClick
        )
    }

    if (borders) {
        LineLayer(
            id = "$id-border",
            source = source,
            sourceLayer = sourceLayer,
            width = const(2.dp),
            color = const(Color.Black),
            minZoom = minZoom,
            maxZoom = maxZoom
        )
    }
}

@Composable
fun ScoreLayer(
    id: String,
    source: VectorTileSource,
    sourceLayer: String,
    featureName: String,
    minZoom: Float,
    maxZoom: Float,
    onClick: FeaturesClickHandler? = null,
) {
    FillLayer(
        id = "$id-fill",
        source = source,
        sourceLayer = sourceLayer,
        minZoom = minZoom,
        maxZoom = maxZoom,
        color = switch(
            input = feature[featureName],
            case(
                label = 1,
                output = const(Color(0xFF, 0xF5, 0xF0))
            ),
            case(
                label = 2,
                output = const(Color(0xFE, 0xE0, 0xD2))
            ),
            case(
                label = 3,
                output = const(Color(0xFC, 0xC5, 0xC0))
            ),
            case(
                label = 4,
                output = const(Color(0xFA, 0x9F, 0xB5))
            ),
            case(
                label = 5,
                output = const(Color(0xF7, 0x68, 0xA1))
            ),
            case(
                label = 6,
                output = const(Color(0xDD, 0x34, 0x97))
            ),
            case(
                label = 7,
                output = const(Color(0xAE, 0x01, 0x7E))
            ),
            case(
                label = 8,
                output = const(Color(0x7A, 0x01, 0x77))
            ),
            case(
                label = 9,
                output = const(Color(0x49, 0x00, 0x6A))
            ),
            case(
                label = 10,
                output = const(Color(0x24, 0x00, 0x3D))
            ),
            fallback = const(Color.White)
        ),
        opacity = const(0.7f),
        onClick = onClick
    )
}