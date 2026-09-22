package org.chordsoft.chezmoi.ui.layers

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.ui.components.createMarkerBitmap
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.rememberVectorTileSource
import org.maplibre.nativeffi.log.LogEvent

@Composable
fun PermisLayer(
    visible: Boolean,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    onMarkerClick: () -> Unit
) {
    if (!visible) return
    val state by mapViewModel.rplsFlow.collectAsStateWithLifecycle()
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val amenagerImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker,
            iconRes = R.drawable.imagesearch_roller_24px,
        ).asImageBitmap()
    }
    val demolirImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker,
            iconRes = R.drawable.destruction_24px
        ).asImageBitmap()
    }
    val construireLogementImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker,
            iconRes = R.drawable.add_home_24px
        ).asImageBitmap()
    }
    val construireNonResidentielImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker,
            iconRes = R.drawable.add_home_work_24px
        ).asImageBitmap()
    }

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

        iconImage = image(amenagerImage),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        onClick = { features ->
            Log.d("ARASH", features.toString())
            scope.launch {
                val dateAutorisation = features[0].properties?.getOrDefault("date_reelle_autorisation", "-").toString()
                val parcelleId = features[0].properties?.getOrDefault("parcelle_id", "-").toString()
                val zoneOP = features[0].properties?.getOrDefault("zone_op", "-").toString()
                val etatPermis = features[0].properties?.getOrDefault("etat_pa", "-").toString()
                val numPermis = features[0].properties?.getOrDefault("num_pa", "-").toString()
                val superficieTerrain = features[0].properties?.getOrDefault("superficie_terrain", "-").toString()
                val commune = features[0].properties?.getOrDefault("comm", "-").toString()
                mapViewModel.updateMarkerInfo(
                    type = MapViewModel.MarkerType.Permis,
                    id = "$numPermis-$parcelleId",
                    value = listOf(
                        "Date autorasion: $dateAutorisation",
                        "Parcelle Id: $parcelleId",
                        "Zone Op: $zoneOP",
                        "Etat permis: $etatPermis",
                        "Superficie terrain: $superficieTerrain",
                        "Commune: $commune"
                    )
                )
                onMarkerClick()
            }

            ClickResult.Consume
        }
    )

    SymbolLayer(
        id = "permis-demolir-symbols",
        source = permisDemolirSource,
        sourceLayer = "permis_demolir_loc",
        minZoom = 10f,

        iconImage = image(demolirImage),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        onClick = { features ->
            Log.d("ARASH", features.toString())
            scope.launch {
                val dateAutorisation = features[0].properties?.getOrDefault("date_reelle_autorisation", "-").toString()
                val parcelleId = features[0].properties?.getOrDefault("parcelle_id", "-").toString()
                val zoneOP = features[0].properties?.getOrDefault("zone_op", "-").toString()
                val etatPermis = features[0].properties?.getOrDefault("etat_pd", "-").toString()
                val numPermis = features[0].properties?.getOrDefault("num_pd", "-").toString()
                val superficieTerrain = features[0].properties?.getOrDefault("superficie_terrain", "-").toString()
                val commune = features[0].properties?.getOrDefault("comm", "-").toString()
                mapViewModel.updateMarkerInfo(
                    type = MapViewModel.MarkerType.Permis,
                    id = "$numPermis-$parcelleId",
                    value = listOf(
                        "Date autorasion: $dateAutorisation",
                        "Parcelle Id: $parcelleId",
                        "Zone Op: $zoneOP",
                        "Etat permis: $etatPermis",
                        "Superficie terrain: $superficieTerrain",
                        "Commune: $commune"
                    )
                )
                onMarkerClick()
            }

            ClickResult.Consume
        }
    )

    SymbolLayer(
        id = "permis-construire-non-residentiels-symbols",
        source = permisConstruireNonResidentielsSource,
        sourceLayer = "permis_construire_non_residentiels_loc",
        minZoom = 10f,

        iconImage = image(construireNonResidentielImage),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        onClick = { features ->
            Log.d("ARASH", features.toString())
            scope.launch {
                val dateAutorisation = features[0].properties?.getOrDefault("date_reelle_autorisation", "-").toString()
                val parcelleId = features[0].properties?.getOrDefault("parcelle_id", "-").toString()
                val zoneOP = features[0].properties?.getOrDefault("zone_op", "-").toString()
                val etatPermis = features[0].properties?.getOrDefault("etat_dau", "-").toString()
                val numPermis = features[0].properties?.getOrDefault("num_dau", "-").toString()
                val superficieTerrain = features[0].properties?.getOrDefault("superficie_terrain", "-").toString()
                val commune = features[0].properties?.getOrDefault("comm", "-").toString()
                mapViewModel.updateMarkerInfo(
                    type = MapViewModel.MarkerType.Permis,
                    id = "$numPermis-$parcelleId",
                    value = listOf(
                        "Date autorasion: $dateAutorisation",
                        "Parcelle Id: $parcelleId",
                        "Zone Op: $zoneOP",
                        "Etat permis: $etatPermis",
                        "Superficie terrain: $superficieTerrain",
                        "Commune: $commune"
                    )
                )
                onMarkerClick()
            }

            ClickResult.Consume
        }
    )

    SymbolLayer(
        id = "permis-logement-symbols",
        source = PermisConstruireResidentielsSource,
        sourceLayer = "permis_construire_residentiels_loc",
        minZoom = 10f,

        iconImage = image(construireLogementImage),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        onClick = { features ->
            Log.d("ARASH", features.toString())
            scope.launch {
                val dateAutorisation = features[0].properties?.getOrDefault("date_reelle_autorisation", "-").toString()
                val parcelleId = features[0].properties?.getOrDefault("parcelle_id", "-").toString()
                val zoneOP = features[0].properties?.getOrDefault("zone_op", "-").toString()
                val etatPermis = features[0].properties?.getOrDefault("etat_dau", "-").toString()
                val numPermis = features[0].properties?.getOrDefault("num_dau", "-").toString()
                val superficieTerrain = features[0].properties?.getOrDefault("superficie_terrain", "-").toString()
                val commune = features[0].properties?.getOrDefault("comm", "-").toString()
                mapViewModel.updateMarkerInfo(
                    type = MapViewModel.MarkerType.Permis,
                    id = "$numPermis-$parcelleId",
                    value = listOf(
                        "Date autorasion: $dateAutorisation",
                        "Parcelle Id: $parcelleId",
                        "Zone Op: $zoneOP",
                        "Etat permis: $etatPermis",
                        "Superficie terrain: $superficieTerrain",
                        "Commune: $commune"
                    )
                )
                onMarkerClick()
            }

            ClickResult.Consume
        }
    )
}