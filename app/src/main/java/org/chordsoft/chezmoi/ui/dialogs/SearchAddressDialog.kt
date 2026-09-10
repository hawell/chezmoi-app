package org.chordsoft.chezmoi.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.chordsoft.chezmoi.R
import org.chordsoft.chezmoi.data.model.Address
import org.chordsoft.chezmoi.data.sources.SourceState
import org.chordsoft.chezmoi.ui.components.LoadingCard
import org.chordsoft.chezmoi.viewmodel.SearchAddressViewModel

@Composable
fun SearchAddressDialog(
    searchAddressViewModel: SearchAddressViewModel,
    onDismiss: () -> Unit,
    onSelect: (lat: Double, lng: Double) -> Unit,
) {
    val query by searchAddressViewModel.query.collectAsStateWithLifecycle()
    val searchResults by searchAddressViewModel.searchResults.collectAsStateWithLifecycle()

    var textFieldValue by remember(query) {
        mutableStateOf(
            TextFieldValue(
                text = query,
                selection = TextRange(query.length)
            )
        )
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = textFieldValue,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { searchAddressViewModel.search() }
                    ),
                    onValueChange = { value ->
                        textFieldValue = value
                        searchAddressViewModel.query.value = value.text
                    },
                    placeholder = { Text("Search...") },
                    leadingIcon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.search_24px),
                            contentDescription = "search",
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                searchAddressViewModel.search()
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_forward_24px),
                                contentDescription = "search"
                            )
                        }
                    },
                )
                when (searchResults) {
                    is SourceState.Loading -> LoadingCard(50.dp, 50.dp)
                    is SourceState.Success<List<Address>> -> {
                        val res = (searchResults as SourceState.Success<List<Address>>).data
                        res.forEach {
                            Card(
                                onClick = { onSelect(it.lat, it.lng) },
                                modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(4.dp),
                                ) {
                                    Text(
                                        text = "${it.number} ${it.address}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${it.postalCode} ${it.city}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}