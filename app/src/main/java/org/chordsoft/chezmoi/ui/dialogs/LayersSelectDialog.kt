package org.chordsoft.chezmoi.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import org.chordsoft.chezmoi.data.sources.Statistics
import org.chordsoft.chezmoi.data.sources.basicMapStyles
import org.chordsoft.chezmoi.viewmodel.SettingsViewModel

data class SelectItem(
    val name: String,
    val icon: Int,
    val get: () -> Boolean,
    val set: () -> Unit
)

@Composable
fun LayersSelectDialog(
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentStyle by settingsViewModel.style.collectAsStateWithLifecycle()
    val activeStatistics by settingsViewModel.statistics.collectAsStateWithLifecycle()
    val layers by settingsViewModel.layers.collectAsStateWithLifecycle()
    val markers by settingsViewModel.markers.collectAsStateWithLifecycle()

    val contours = listOf(
        SelectItem(
            name = "Limite Administrative",
            icon = R.drawable.location_city_24px,
            get = { layers.limiteAdministrative },
            set = { scope.launch { settingsViewModel.setLayers(layers.copy(limiteAdministrative = !layers.limiteAdministrative))} }
        ),
        SelectItem(
            name = "Cadastre",
            icon = R.drawable.domain_24px,
            get = { layers.cadastre },
            set = { scope.launch { settingsViewModel.setLayers(layers.copy(cadastre = !layers.cadastre))} }
        ),
    )
    val risques = listOf(
        SelectItem(
            name = "Permis de construction",
            icon = R.drawable.approval_24px,
            get = { markers.permis },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(permis = !markers.permis))} }
        ),
        SelectItem(
            name = "Lignes électriques haute tension",
            icon = R.drawable.bolt_24px,
            get = { markers.powerLines },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(powerLines = !markers.powerLines))} }
        ),
        SelectItem(
            name = "Gazoduc",
            icon = R.drawable.valve_24px,
            get = { markers.gas },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(gas = !markers.gas))} }
        ),
        SelectItem(
            name = "Installations industrielles",
            icon = R.drawable.factory_24px,
            get = { markers.factories },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(factories = !markers.factories))} }
        ),
    )
    val markerItems = listOf(
        SelectItem(
            name = "Rpls",
            icon = R.drawable.shield_with_house_24px,
            get = { markers.rpls },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(rpls = !markers.rpls))} }
        ),
        SelectItem(
            name = "Valeur Fonciere",
            icon = R.drawable.euro_symbol_24px,
            get = { markers.valeurFonciere },
            set = { scope.launch { settingsViewModel.setMarkers(markers.copy(valeurFonciere = !markers.valeurFonciere))} }
        )
    )
    val mapStyles = basicMapStyles.map { (name, style) ->
        SelectItem(
            name = name,
            icon = 0,
            set = { scope.launch { settingsViewModel.setStyle(style) }},
            get = { style.name == currentStyle.name }
        )
    }
    val statistics = listOf(
        SelectItem(
            name = "aucun",
            icon = 0,
            get = { activeStatistics == Statistics.None },
            set = { scope.launch { settingsViewModel.setStatistics(Statistics.None) }}
        ),
        SelectItem(
            name = "pauvreté",
            icon = 0,
            get = { activeStatistics == Statistics.Poverty },
            set = { scope.launch { settingsViewModel.setStatistics(Statistics.Poverty) }}
        ),
        SelectItem(
            name = "population",
            icon = 0,
            get = { activeStatistics == Statistics.Population },
            set = { scope.launch { settingsViewModel.setStatistics(Statistics.Population) }}
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
                modifier = Modifier.fillMaxSize().verticalScroll(state = rememberScrollState())
            ) {
                markerItems.forEach { item ->
                    MarkerToggle(item)
                }
                ToggleGroup(
                    title = "limites territoriales",
                    icon = R.drawable.outdoor_garden_24px,
                    items = contours
                )
                ToggleGroup(
                    title = "Risques et désagréments",
                    icon = R.drawable.warning_24px,
                    items = risques
                )
                SelectGroup(
                    title = "Style de carte",
                    icon = R.drawable.map_24px,
                    items = mapStyles
                )
                SelectGroup(
                    title = "Statistiques",
                    icon = R.drawable.area_chart_24px,
                    items = statistics
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MarkerToggle(item: SelectItem) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            item.set()
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

@Composable
fun SelectGroup(title: String, icon: Int, items: List<SelectItem>) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(end = 8.dp, start = 8.dp),
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = title
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp)
    ) {
        items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = item.get(),
                    onClick = {
                        item.set()
                    },
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ToggleGroup(title: String, icon: Int, items: List<SelectItem>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(end = 8.dp, start = 8.dp),
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = title
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp)
    ) {
        items.forEach { item ->
            MarkerToggle(item)
        }
    }
}