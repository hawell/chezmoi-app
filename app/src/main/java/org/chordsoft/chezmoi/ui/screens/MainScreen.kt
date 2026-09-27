package org.chordsoft.chezmoi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.ui.components.LocationRationale
import org.chordsoft.chezmoi.ui.components.OverlayButton
import org.chordsoft.chezmoi.ui.dialogs.IrisInfoDialog
import org.chordsoft.chezmoi.ui.dialogs.LayersSelectDialog
import org.chordsoft.chezmoi.ui.dialogs.MarkerInfoDialog
import org.chordsoft.chezmoi.ui.dialogs.SearchAddressDialog
import org.chordsoft.chezmoi.ui.dialogs.SettingsDialog
import org.chordsoft.chezmoi.ui.layers.AdminLayer
import org.chordsoft.chezmoi.ui.layers.CadastreLayer
import org.chordsoft.chezmoi.ui.layers.PermisLayer
import org.chordsoft.chezmoi.ui.layers.RplsLayer
import org.chordsoft.chezmoi.ui.layers.ValeurFonciereLayer
import org.chordsoft.chezmoi.viewmodel.MapViewModel
import org.chordsoft.chezmoi.viewmodel.SearchAddressViewModel
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.location.LocationPermission
import org.maplibre.compose.location.LocationPuck
import org.maplibre.compose.location.rememberDefaultHeadingProvider
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.location.rememberSystemSettingsLauncher
import org.maplibre.compose.map.CameraConstraints
import org.maplibre.compose.map.LocalMapState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.ScaleBar
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun MainScreen(
    modifier: Modifier,
    mapViewModel: MapViewModel,
    searchAddressViewModel: SearchAddressViewModel,
    settingsViewModel: SettingsViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val markerInfoState = mapViewModel.markerInfo
    val locationProvider = rememberDefaultLocationProvider()
    val headingProvider = rememberDefaultHeadingProvider()
    val locationState =
        rememberLocationState(
            provider = locationProvider,
            headingProvider = headingProvider,
        )

    var showSearchAddressDialog by remember { mutableStateOf(false) }
    var showMarkerInfoDialog by remember { mutableStateOf(false) }
    var showLayersSelectDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showIrisInfoDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val paris = Position(latitude = 48.8540819, longitude = 2.3405084)
    val franceBounds = BoundingBox(
        northeast = Position(latitude = 51.5, longitude = 9.8),
        southwest = Position(latitude = 41.0, longitude = -5.5)
    )
    val style by settingsViewModel.style.collectAsStateWithLifecycle()
    val layers by settingsViewModel.layers.collectAsStateWithLifecycle()
    val markers by settingsViewModel.markers.collectAsStateWithLifecycle()

    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri(style.file),
        initialCameraPosition = CameraPosition(target = paris, zoom = 8.0)
    ) {
        val mapState = checkNotNull(LocalMapState.current)

        AdminLayer(
            visible = layers.limiteAdministrative,
            mapState = mapState,
            mapViewModel = mapViewModel,
            onIrisClick = {
                showIrisInfoDialog = true
            }
        )

        CadastreLayer(
            visible = layers.cadastre,
            mapViewModel = mapViewModel,
            onMarkerClick = {
                showMarkerInfoDialog = true
            }
        )

        RplsLayer(
            visible = markers.rpls,
            mapState = mapState,
            mapViewModel = mapViewModel,
            settingsViewModel = settingsViewModel,
            onMarkerClick = {
                showMarkerInfoDialog = true
            }
        )

        PermisLayer(
            visible = markers.permis,
            mapViewModel = mapViewModel,
            settingsViewModel = settingsViewModel,
            onMarkerClick = {
                showMarkerInfoDialog = true
            }
        )

        ValeurFonciereLayer(
            visible = markers.valeurFonciere,
            mapViewModel = mapViewModel,
            settingsViewModel = settingsViewModel,
            onMarkerClick = {
                showMarkerInfoDialog = true
            }
        )

        LocationPuck(
            idPrefix = "user",
            locationState = locationState,
        )
    }

    LaunchedEffect(mapState.cameraPosition) {
        snapshotFlow {
            mapState.cameraPosition to mapState.viewport
        }
            .debounce(300.milliseconds)
            .collect { (position, viewport) ->
                viewport?.let {
                    val bounds = it.visibleBounds
                    mapViewModel.onCameraChange(
                        bounds.south,
                        bounds.west,
                        bounds.north,
                        bounds.east,
                        position.zoom.toFloat(),
                        layers,
                        markers
                    )
                }
            }
    }
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
            state = mapState,
            cameraConstraints = CameraConstraints(boundingBox = franceBounds),
            overlay = {
                Surface(modifier = Modifier.align(Alignment.TopCenter)) {
                    Text(text = mapState.cameraPosition.zoom.toString())
                }
                ScaleBar(
                    this.mapState.viewport?.metersPerDpAtTarget ?: 0.0,
                    modifier = Modifier.align(Alignment.BottomStart),
                ) // (1)!
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
/*
                    CompassButton(
                        style = CompassDefaults.style()
                            .copy(containerColor = Color.Gray.copy(alpha = 0.8f))
                    )
*/
                    OverlayButton(
                        icon = R.drawable.my_location_24px,
                        onClick = {
                            locationState.lastLocation?.let {
                                scope.launch {
                                    this@MaplibreMap.mapState.animateCameraPosition(
                                        CameraPosition(
                                            target = it.position,
                                            zoom = 15.0
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
                OverlayButton(
                    icon = R.drawable.search_24px,
                    modifier = Modifier.align(Alignment.TopStart),
                    onClick = {
                        showSearchAddressDialog = true
                    }
                )

                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OverlayButton(
                        icon = R.drawable.layers_24px,
                        onClick = {
                            showLayersSelectDialog = true
                        }
                    )
                    OverlayButton(
                        icon = R.drawable.settings_24px,
                        onClick = {
                            showSettingsDialog = true
                        }
                    )
                }
            },
        )

        if (showSearchAddressDialog) {
            SearchAddressDialog(
                searchAddressViewModel = searchAddressViewModel,
                onDismiss = { showSearchAddressDialog = false },
                onSelect = { lat, lng ->
                    keyboardController?.hide()
                    showSearchAddressDialog = false
                    scope.launch {
                        mapState.animateCameraPosition(
                            CameraPosition(
                                target = Position(latitude = lat, longitude = lng),
                                zoom = 16.0
                            )
                        )
                    }
                }
            )
        }
        if (showMarkerInfoDialog) {
            MarkerInfoDialog(markerInfoState, { showMarkerInfoDialog = false })
        }
        if (showLayersSelectDialog) {
            LayersSelectDialog(
                settingsViewModel = settingsViewModel,
                onDismiss = { showLayersSelectDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                settingsViewModel = settingsViewModel,
                onDismiss = { showSettingsDialog = false }
            )
        }

        if (showIrisInfoDialog) {
            IrisInfoDialog(
                mapViewModel = mapViewModel,
                onDismiss = { showIrisInfoDialog = false }
            )
        }
    }
}