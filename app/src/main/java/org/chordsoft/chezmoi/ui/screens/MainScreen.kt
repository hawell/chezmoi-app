package org.chordsoft.chezmoi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.ui.components.LocationRationale
import org.chordsoft.chezmoi.ui.components.OverlayButton
import org.chordsoft.chezmoi.ui.components.RplsLayer
import org.chordsoft.chezmoi.ui.dialogs.MarkerInfoDialog
import org.chordsoft.chezmoi.ui.dialogs.SearchAddressDialog
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SearchAddressViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.layers.RasterLayer
import org.maplibre.compose.location.LocationPermission
import org.maplibre.compose.location.LocationPuck
import org.maplibre.compose.location.mostAccurateBearing
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberDefaultOrientationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.location.rememberSystemSettingsLauncher
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.overlay.CompassButton
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.ScaleBar
import org.maplibre.compose.sources.rememberRasterSource
import org.maplibre.compose.style.BaseStyle.Json
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun MainScreen(
    modifier: Modifier,
    mapViewModel: MapViewModel,
    searchAddressViewModel: SearchAddressViewModel,
) {
    val rplsFlow = mapViewModel.rplsFlow
    val scope = rememberCoroutineScope()
    val markerInfoState = mapViewModel.markerInfo.collectAsStateWithLifecycle()
    var showSearchAddressDialog by remember { mutableStateOf(false) }
    var showMarkerInfoDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val paris = Position(latitude = 48.8540819, longitude = 2.3405084)
    val franceBounds = BoundingBox(
        northeast = Position(latitude = 51.5, longitude = 9.8),
        southwest = Position(latitude = 41.0, longitude = -5.5)
    )
    val onMarkerClick: (id: Int, type: MapViewModel.MarkerType) -> Unit = { id, type ->
        mapViewModel.updateMarkerInfo(id, type)
        showMarkerInfoDialog = true
    }

    val camera = rememberCameraState(firstPosition = CameraPosition(target = paris, zoom = 8.0))
    LaunchedEffect(camera) {
        snapshotFlow {
            camera.position to camera.viewport
        }
            .debounce(300.milliseconds)
            .collect { (position, viewport) ->
                viewport?.let {
                    val bounds = it.visibleBoundingBox
                    mapViewModel.onCameraChange(bounds.south, bounds.west, bounds.north, bounds.east, position.zoom.toFloat())
                }
            }
    }
    val locationProvider = rememberDefaultLocationProvider()
    val orientationProvider =
        rememberDefaultOrientationProvider() // optional: get device orientation from sensors
    val locationState =
        rememberLocationState(
            provider = locationProvider,
            orientationProvider = orientationProvider,
        )

    val settings = rememberSystemSettingsLauncher()
    val permission = locationState.permission
    if (permission is LocationPermission.NotGranted) {
        when {
            permission.shouldShowRationale ->
                LocationRationale(onAccept = locationState::requestPermission)
            permission.canRequest != false ->
                Button(onClick = locationState::requestPermission) { Text("Use my location") }
            settings.canOpenApplicationSettings ->
                Button(onClick = { settings.openApplicationSettings() }) { Text("Open settings") }
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        MaplibreMap(
            //baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
            //baseStyle = BaseStyle.Uri("https://data.geopf.fr/annexes/ressources/vectorTiles/styles/PLAN.IGN/standard.json"),
            baseStyle = Json {
                put("version", 8)
                put("name", "MapLibre Compose")
                put("glyphs", "https://demotiles.maplibre.org/font/{fontstack}/{range}.pbf")
                putJsonObject("metadata") {}
                putJsonObject("sources") {}
                putJsonArray("layers") {}
            },
            cameraState = camera,
            boundingBox = franceBounds,
            overlay = MapOverlay {
                ScaleBar(
                    cameraState.viewport?.metersPerDpAtTarget ?: 0.0,
                    modifier = Modifier.align(Alignment.BottomStart),
                ) // (1)!
                CompassButton(cameraState, modifier = Modifier.align(Alignment.TopEnd))
                OverlayButton(
                    icon = R.drawable.my_location_24px,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = {
                        locationState.location?.let {
                            scope.launch {
                                camera.animateTo(CameraPosition(target = it.position.value, zoom = 15.0))
                            }
                        }
                    }
                )
                OverlayButton(
                    icon = R.drawable.search_24px,
                    modifier = Modifier.align(Alignment.TopStart),
                    onClick = {
                        showSearchAddressDialog = true
                    }
                )
            },
        ) {
            val osmSource = rememberRasterSource(
                tiles = listOf(
                    "https://a.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png",
                    "https://b.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png",
                    "https://c.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png",
                ),
                tileSize = 256,
            )

            RasterLayer(
                id = "osmfr-layer",
                source = osmSource,
            )

            RplsLayer(rplsFlow, camera, onMarkerClick)

            LocationPuck(
                idPrefix = "user",
                location = locationState.location,
                // optional: combine course and orientation bearing
                bearing = locationState.mostAccurateBearing(),
                cameraState = camera,
            )
        }
    }

    if (showSearchAddressDialog) {
        SearchAddressDialog(
            searchAddressViewModel = searchAddressViewModel,
            onDismiss = { showSearchAddressDialog = false },
            onSelect = { lat, lng ->
                keyboardController?.hide()
                showSearchAddressDialog = false
                scope.launch {
                    camera.animateTo(
                        CameraPosition(target = Position(latitude = lat, longitude = lng), zoom = 16.0)
                    )
                }
            }
        )
    }
    if (showMarkerInfoDialog) {
        MarkerInfoDialog({ showMarkerInfoDialog = false }) {
            when (markerInfoState.value.type) {
                MapViewModel.MarkerType.Unknown -> {
                    Text("Loading...")
                }

                MapViewModel.MarkerType.RplsMarker -> {
                    markerInfoState.value.text.forEach {
                        Text(it)
                    }
                }
            }
        }
    }
}