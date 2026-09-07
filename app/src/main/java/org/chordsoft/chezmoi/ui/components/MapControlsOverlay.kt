package org.chordsoft.chezmoi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.ui.dialogs.SearchAddressDialog
import org.chordsoft.chezmoi.viewmodel.SearchAddressViewModel

@Composable
fun BoxScope.MapControlsOverlay(
    searchAddressViewModel: SearchAddressViewModel,
    cameraPositionState: CameraPositionState,
    onMyLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var showSearchAddressDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.statusBarsPadding().padding(start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = {
                showSearchAddressDialog = true
            },
            modifier = Modifier.size(44.dp)
        ) {
            Text("\uD83D\uDD0D")
        }
    }
    Column(
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Compass reset (north up)
        FloatingActionButton(
            onClick = {
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder(
                                cameraPositionState.position
                            )
                                .bearing(0f)
                                .tilt(0f)
                                .build()
                        ),
                        durationMs = 500
                    )
                }
            },
            modifier = Modifier.size(44.dp)
        ) {
            Text("🧭")
        }

        // My location
        FloatingActionButton(
            onClick = onMyLocationClick,
            modifier = Modifier.size(44.dp)
        ) {
            Text("◎")
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
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder(
                                cameraPositionState.position
                            )
                                .target(LatLng(lat, lng))
                                .zoom(16.0f)
                                .build()
                        ),
                        durationMs = 500
                    )
                }
            }
        )
    }
}