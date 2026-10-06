package org.chordsoft.chezmoi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.chordsoft.chezmoi.data.api.RetrofitInstance
import org.chordsoft.chezmoi.data.model.Rpls
import org.chordsoft.chezmoi.data.sources.GeoJsonDataSource
import org.chordsoft.chezmoi.data.sources.Layers
import org.chordsoft.chezmoi.data.sources.Markers
import org.chordsoft.chezmoi.data.sources.ZoomLevel
import org.maplibre.spatialk.geojson.Position

class MapViewModel: ViewModel() {

    data class Pin(
        val id: Int,
        val label: String,
        val position: Position,
        val count: Int
    )

    enum class MarkerType {
        Unknown,
        RplsMarker,
        CadastreParcelle,
        ValeurFonciere,
        Permis,
        PollutedSite,
        Education,
        Health,
    }

    data class MarkerInfoItem(
        val id: String = "",
        val data: List<String> = emptyList()
    )

    data class MarkerInfo(
        val type: MarkerType = MarkerType.Unknown,
        val items: List<MarkerInfoItem> = emptyList()
    )
    private val _markerInfo = MutableStateFlow(MarkerInfo())
    val markerInfo: StateFlow<MarkerInfo> = _markerInfo
    fun updateMarkerInfo(markerInfo: MarkerInfo) {
        _markerInfo.value = markerInfo
    }

    data class IrisInfo(
        val id: String = "",
        val name: String = "",
        val commune: String = ""
    )
    private val _irisInfo = MutableStateFlow(IrisInfo())
    val irisInfo: StateFlow<IrisInfo> = _irisInfo
    fun updateIrisInfo(irisInfo: IrisInfo) {
        _irisInfo.value = irisInfo
    }

    private val rplsSource = GeoJsonDataSource(
        apiCall = { viewPort ->
            api.getRpls(viewPort.south, viewPort.west, viewPort.north, viewPort.east, viewPort.zoom)
        },
        scope = viewModelScope
    )
    val rplsFlow = rplsSource.flow

    private val _zoomLevel = MutableStateFlow(ZoomLevel.Tele)
    private val api = RetrofitInstance.api

    fun onCameraChange(
        south: Double,
        west: Double,
        north: Double,
        east: Double,
        zoom: Float,
        layers: Layers,
        markers: Markers
    ) {
        _zoomLevel.value = when {
            zoom >= 15.0 -> ZoomLevel.Tele
            zoom >= 13.0 -> ZoomLevel.Wide
            else -> ZoomLevel.City
        }
        if (markers.rpls) {
            rplsSource.updateViewPort(south, west, north, east, zoom)
        }
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
