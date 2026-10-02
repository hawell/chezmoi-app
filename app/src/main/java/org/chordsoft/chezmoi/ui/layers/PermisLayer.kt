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
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.VectorTileSource

@Composable
fun PermisLayer(
    visible: Boolean,
    sources:  Map<String, VectorTileSource>,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    onMarkerClick: () -> Unit
) {
    if (!visible) return
    val state by mapViewModel.rplsFlow.collectAsStateWithLifecycle()
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val minZoom = 15f
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val amenagerImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_permis,
            iconRes = R.drawable.imagesearch_roller_24px,
        ).asImageBitmap()
    }
    val demolirImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_permis,
            iconRes = R.drawable.destruction_24px
        ).asImageBitmap()
    }
    val construireLogementImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_permis,
            iconRes = R.drawable.add_home_24px
        ).asImageBitmap()
    }
    val construireNonResidentielImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_permis,
            iconRes = R.drawable.add_home_work_24px
        ).asImageBitmap()
    }

    val permisAmenagerSource = sources["permis_amenager"]!!
    val permisDemolirSource = sources["permis_demolir"]!!
    val permisConstruireNonResidentielsSource = sources["permis_construire_non_residentiels"]!!
    val PermisConstruireResidentielsSource = sources["permis_construire_residentiels"]!!

    SymbolLayer(
        id = "permis-amenager-symbols",
        sourceLayer = "permis_amenager_loc",
        source = permisAmenagerSource,
        minZoom = minZoom,

        iconImage = image(amenagerImage),
        iconAnchor = const(SymbolAnchor.Bottom),
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
                    MapViewModel.MarkerInfo(
                        type = MapViewModel.MarkerType.Permis,
                        items = listOf(
                            MapViewModel.MarkerInfoItem(
                                id = "$numPermis-$parcelleId",
                                data = listOf(
                                    "Date autorasion: $dateAutorisation",
                                    "Parcelle Id: $parcelleId",
                                    "Zone Op: $zoneOP",
                                    "Etat permis: $etatPermis",
                                    "Superficie terrain: $superficieTerrain",
                                    "Commune: $commune"
                                )
                            )
                        ),
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
        minZoom = minZoom,

        iconImage = image(demolirImage),
        iconAnchor = const(SymbolAnchor.Bottom),
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
                    MapViewModel.MarkerInfo(
                        type = MapViewModel.MarkerType.Permis,
                        items = listOf(
                            MapViewModel.MarkerInfoItem(
                                id = "$numPermis-$parcelleId",
                                data = listOf(
                                    "Date autorasion: $dateAutorisation",
                                    "Parcelle Id: $parcelleId",
                                    "Zone Op: $zoneOP",
                                    "Etat permis: $etatPermis",
                                    "Superficie terrain: $superficieTerrain",
                                    "Commune: $commune"
                                )
                            )
                        ),
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
        minZoom = minZoom,

        iconImage = image(construireNonResidentielImage),
        iconAnchor = const(SymbolAnchor.Bottom),
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
                    MapViewModel.MarkerInfo(
                        type = MapViewModel.MarkerType.Permis,
                        items = listOf(
                            MapViewModel.MarkerInfoItem(
                                id = "$numPermis-$parcelleId",
                                data = listOf(
                                    "Date autorasion: $dateAutorisation",
                                    "Parcelle Id: $parcelleId",
                                    "Zone Op: $zoneOP",
                                    "Etat permis: $etatPermis",
                                    "Superficie terrain: $superficieTerrain",
                                    "Commune: $commune"
                                )
                            )
                        ),
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
        minZoom = minZoom,

        iconImage = image(construireLogementImage),
        iconAnchor = const(SymbolAnchor.Bottom),
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
                    MapViewModel.MarkerInfo(
                        type = MapViewModel.MarkerType.Permis,
                        items = listOf(
                            MapViewModel.MarkerInfoItem(
                                id = "$numPermis-$parcelleId",
                                data = listOf(
                                    "Date autorasion: $dateAutorisation",
                                    "Parcelle Id: $parcelleId",
                                    "Zone Op: $zoneOP",
                                    "Etat permis: $etatPermis",
                                    "Superficie terrain: $superficieTerrain",
                                    "Commune: $commune"
                                )
                            )
                        ),
                    )
                )
                onMarkerClick()
            }

            ClickResult.Consume
        }
    )
}