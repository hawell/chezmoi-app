package org.chordsoft.chezmoi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch

@Composable
fun BoxScope.MapControlsOverlay(
    cameraPositionState: CameraPositionState,
    onMyLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
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
}