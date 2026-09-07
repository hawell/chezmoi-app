package org.chordsoft.chezmoi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.data.api.RetrofitInstance
import org.chordsoft.chezmoi.data.model.Address
import org.chordsoft.chezmoi.data.model.SourceState

class SearchAddressViewModel: ViewModel() {
    private val api = RetrofitInstance.api

    val query = MutableStateFlow("")

    private val _searchResults = MutableStateFlow< SourceState<List<Address>>>(SourceState.Empty)
    val searchResults: StateFlow<SourceState<List<Address>>> = _searchResults.asStateFlow()

    fun search() {
        _searchResults.value = SourceState.Loading
        viewModelScope.launch {
            try {
                val res = api.searchAddress(query.value)
                _searchResults.value = SourceState.Success(res.data)
            } catch (e: Exception) {
                _searchResults.value = SourceState.Error(e.message ?: e.stackTraceToString())
            }
        }
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                SearchAddressViewModel()
            }
        }
    }
}