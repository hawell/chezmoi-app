package org.chordsoft.chezmoi.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.viewmodel.MapViewModel

@Composable
fun MarkerInfoDialog(
    markerInfoState: StateFlow<MapViewModel.MarkerInfo>,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = markerInfoState.collectAsStateWithLifecycle()
    Dialog(onDismissRequest = onDismiss) {
        val (title, icon) = when (state.value.type) {
            MapViewModel.MarkerType.CadastreParcelle -> Pair("Cadastre", R.drawable.domain_24px)
            MapViewModel.MarkerType.RplsMarker -> Pair("Logement Sociaux", R.drawable.shield_with_house_24px)
            MapViewModel.MarkerType.Permis -> Pair("Permis", R.drawable.approval_24px)
            MapViewModel.MarkerType.ValeurFonciere -> Pair("Valeur Fonciere", R.drawable.euro_symbol_24px)
            MapViewModel.MarkerType.PollutedSite -> Pair("Sites pollués", R.drawable.dangerous_24px)
            MapViewModel.MarkerType.Education -> Pair("Éducation", R.drawable.school_24px)
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
                if (state.value.items.size > 1) {
                    val pageCount = state.value.items.size
                    val pagerState = rememberPagerState(pageCount = { state.value.items.size })
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1
                                    )
                                }
                            },
                            enabled = pagerState.currentPage > 0
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_back_24px),
                                contentDescription = "Previous page"
                            )
                        }

                        Text(
                            text = "Page ${pagerState.currentPage + 1} of $pageCount",
                            style = MaterialTheme.typography.titleMedium
                        )

                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1
                                    )
                                }
                            },
                            enabled = pagerState.currentPage < pageCount - 1
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_forward_24px),
                                contentDescription = "Next page"
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { page ->
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            state.value.items[page].data.forEach {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    state.value.items[0].data.forEach {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}