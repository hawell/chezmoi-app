package org.chordsoft.chezmoi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.data.api.RetrofitInstance
import org.chordsoft.chezmoi.data.model.Cluster
import org.chordsoft.chezmoi.data.model.Rpls
import org.chordsoft.chezmoi.data.sources.SourceState
import org.chordsoft.chezmoi.data.sources.ViewPortDataSource
import org.chordsoft.chezmoi.data.sources.ZoomLevel

class MapViewModel: ViewModel() {

    data class Pin(
        val id: Int,
        val label: String,
        val position: LatLng,
        val count: Int
    )

    enum class MarkerType {
        Unknown,
        RplsMarker
    }

    data class MarkerInfo(
        val id: Int = 0,
        val type: MarkerType = MarkerType.Unknown,
        val text: List<String> = emptyList()
    )

    private val _markerInfo = MutableStateFlow(MarkerInfo())
    val markerInfo: StateFlow<MarkerInfo> = _markerInfo

    fun updateMarkerInfo(id: Int, type: MarkerType) {
        viewModelScope.launch {
            try {
                val res = api.getRplsDetails(id)
                _markerInfo.value = MarkerInfo(
                    id = res.data.id,
                    type = MarkerType.RplsMarker,
                    text = listOf(
                        "${res.data.number} ${res.data.address}",
                        "${res.data.postalCode} ${res.data.city}",
                        "Année construction: ${res.data.constructionYear}",
                        "Nbr PLAI: ${res.data.numPlai}",
                        "Nbr PLUS: ${res.data.numPlus}",
                        "Nbr PLS: ${res.data.numPls}",
                        "Nbr PLI: ${res.data.numPli}",
                        "Nbr Inconnu: ${res.data.numUnknown}",
                    )
                )
            } catch (e: Exception) {
            }
        }
    }

    private val rplsSource = ViewPortDataSource(
        apiCall = { viewPort ->
            api.getRpls(viewPort.south, viewPort.west, viewPort.north, viewPort.east, viewPort.zoom)
        },
        scope = viewModelScope
    )

    data class PinSet(
        val rpls: Map<String, Pin> = emptyMap()
    )

    private val rplsCache = mutableMapOf<String, Pin>()

    private val _zoomLevel = MutableStateFlow(ZoomLevel.Tele)
    private val api = RetrofitInstance.api

    @OptIn(ExperimentalCoroutinesApi::class)
    val pinFlow: StateFlow<PinSet> = rplsSource.flow
        .map { rplsState ->
            if (rplsState is SourceState.Success<List<Cluster>>) {
                rplsCache.clear()
                rplsState.data.forEach { cluster ->
                    val id = cluster.id()
                    rplsCache.getOrPut(id) {
                        Pin(
                            id = cluster.id,
                            label = cluster.label(),
                            position = LatLng(cluster.centerLatitude, cluster.centerLongitude),
                            count = cluster.count
                        )
                    }
                }
            }
            PinSet(rplsCache.toMap())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PinSet()
        )

    fun onCameraChange(
        south: Double,
        west: Double,
        north: Double,
        east: Double,
        zoom: Float
    ) {
        _zoomLevel.value = when {
            zoom >= 15.0 -> ZoomLevel.Tele
            zoom >= 13.0 -> ZoomLevel.Wide
            else -> ZoomLevel.City
        }
        rplsSource.updateViewPort(south, west, north, east, zoom)
    }

    suspend fun getRplsDetails(id: Int): Rpls? {
        try {
            val res = api.getRplsDetails(id)
            return res.data
        } catch (e: Exception) {
            Log.e("APICALL", e.message ?: e.stackTraceToString())
        }
        return null
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                MapViewModel()
            }
        }
    }
}
