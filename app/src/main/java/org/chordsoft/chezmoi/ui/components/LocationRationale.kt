package org.chordsoft.chezmoi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LocationRationale(
    onAccept: () -> Unit
) {
    Column {
        Text("Location permission is required")

        Text(
            "We use your location to provide nearby services."
        )

        Button(onClick = onAccept) {
            Text("Continue")
        }
    }
}