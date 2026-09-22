package org.chordsoft.chezmoi.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.viewmodel.MapViewModel

@Composable
fun MarkerInfoDialog(
    markerInfoState: StateFlow<MapViewModel.MarkerInfo>,
    onDismiss: () -> Unit,
) {
    val state = markerInfoState.collectAsStateWithLifecycle()
    Dialog(onDismissRequest = onDismiss) {
        val (title, icon) = when (state.value.type) {
            MapViewModel.MarkerType.CadastreParcelle -> Pair("Cadastre", R.drawable.domain_24px)
            MapViewModel.MarkerType.RplsMarker -> Pair("Logement Sociaux", R.drawable.shield_with_house_24px)
            MapViewModel.MarkerType.Permis -> Pair("Permis", R.drawable.approval_24px)
            MapViewModel.MarkerType.Unknown -> Pair("Loading...", R.drawable.hourglass_top_24px)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(imageVector = ImageVector.vectorResource(icon), contentDescription = null)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                state.value.text.forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}