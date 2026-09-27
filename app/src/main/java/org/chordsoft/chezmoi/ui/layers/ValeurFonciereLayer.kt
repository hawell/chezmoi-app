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

@Composable
fun ValeurFonciereLayer(
    visible: Boolean,
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
    val sellImage = remember {
        createMarkerBitmap(
            context = context,
            pinRes = R.drawable.ic_map_marker_valeur_fonciere,
            iconRes = R.drawable.euro_symbol_24px,
        ).asImageBitmap()
    }

    val valeurFonciereSource = rememberVectorTileSource(
        tiles = listOf(
            "http://192.168.1.34:4000/valeur_fonciere/{z}/{x}/{y}.pbf",
        )
    )

    SymbolLayer(
        id = "valeur-fonciere-symbols",
        source = valeurFonciereSource,
        sourceLayer = "valeur_fonciere",
        minZoom = minZoom,

        iconImage = image(sellImage),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true),

        onClick = { features ->
            Log.d("ARASH", features.toString())
            val items = features.map { feature ->
                val idMutation = feature.properties?.getOrDefault("id_mutation", "-").toString()
                val dateMutation = feature.properties?.getOrDefault("date_mutation", "-").toString()
                val numeroDisposition = feature.properties?.getOrDefault("numero_disposition", "-").toString()
                val natureMutation = feature.properties?.getOrDefault("nature_mutation", "-").toString()
                val valeurFonciere = feature.properties?.getOrDefault("valeur_fonciere", "-").toString()
                val adresseNumero = feature.properties?.getOrDefault("adresse_numero", "-").toString()
                val adresseSuffixe = feature.properties?.getOrDefault("adresse_suffixe", "-").toString()
                val adresseNomVoie = feature.properties?.getOrDefault("adresse_nom_voie", "-").toString()
                val adresseCodeVoie = feature.properties?.getOrDefault("adresse_code_voie", "-").toString()
                val codePostal = feature.properties?.getOrDefault("code_postal", "-").toString()
                val nomCommune = feature.properties?.getOrDefault("nom_commune", "-").toString()
                val idParcelle = feature.properties?.getOrDefault("id_parcelle", "-").toString()
                val typeLocal = feature.properties?.getOrDefault("type_local", "-").toString()
                val surfaceReelleBati = feature.properties?.getOrDefault("surface_reelle_bati", "-").toString()
                val nombrePiecesPrincipales = feature.properties?.getOrDefault("nombre_pieces_principales", "-").toString()
                val surfaceTerrain = feature.properties?.getOrDefault("surface_terrain", "-").toString()
                MapViewModel.MarkerInfoItem(
                    id = idMutation,
                    data = listOf(
                        "$adresseNumero $adresseSuffixe $adresseNomVoie",
                        "$codePostal $nomCommune",
                        "Date: $dateMutation",
                        "Valeur fonciere: $valeurFonciere",
                        "Id parcelle: $idParcelle",
                        "Type local: $typeLocal",
                        "Surface batiment: $surfaceReelleBati",
                        "Surface terrain: $surfaceTerrain",
                        "Nbr piece: $nombrePiecesPrincipales",
                    )
                )
            }
            scope.launch {
                mapViewModel.updateMarkerInfo(
                    MapViewModel.MarkerInfo(
                        type = MapViewModel.MarkerType.ValeurFonciere,
                        items = items
                    )
                )
                onMarkerClick()
            }

            ClickResult.Consume
        }
    )
}