package org.chordsoft.chezmoi.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel

@Composable
fun LayersSelectDialog(
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val layers by settingsViewModel.layers.collectAsStateWithLifecycle()
    val markers by settingsViewModel.markers.collectAsStateWithLifecycle()

    data class SelectItem(
        val name: String,
        val icon: Int,
        val get: () -> Boolean,
        val set: () -> Unit
    )
    val items = listOf(
        SelectItem(
            name = "Limite Administrative",
            icon = R.drawable.outdoor_garden_24px,
            get = { layers.limiteAdministrative },
            set = { scope.launch { settingsViewModel.setLayers(layers.copy(limiteAdministrative = !layers.limiteAdministrative))} }
        ),
        SelectItem(
            name = "Cadastre",
            icon = R.drawable.domain_24px,
            get = { layers.cadastre },
            set = { scope.launch { settingsViewModel.setLayers(layers.copy(cadastre = !layers.cadastre))} }
        ),
        SelectItem(
            name = "Rpls",
            icon = R.drawable.shield_with_house_24px,
            get = { markers.rpls },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(rpls = !markers.rpls))} }
        ),
        SelectItem(
            name = "Permis",
            icon = R.drawable.approval_24px,
            get = { markers.permis },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(permis = !markers.permis))} }
        ),
        SelectItem(
            name = "Valeur Fonciere",
            icon = R.drawable.euro_symbol_24px,
            get = { markers.valeurFonciere },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(valeurFonciere = !markers.valeurFonciere))} }
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                items.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            scope.launch {
                                item.set()
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(Modifier.weight(1f))
                            Checkbox(
                                checked = item.get(),
                                modifier = Modifier.align(Alignment.CenterVertically),
                                onCheckedChange = {
                                    item.set()
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }
    }
}