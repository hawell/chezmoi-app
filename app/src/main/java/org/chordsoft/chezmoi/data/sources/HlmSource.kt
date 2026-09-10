package org.chordsoft.chezmoi.data.sources

import android.util.Log
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.chordsoft.chezmoi.data.api.ApiResponse
import org.chordsoft.chezmoi.data.model.ViewPort
import org.maplibre.spatialk.geojson.GeoJsonObject
import kotlin.time.Duration.Companion.milliseconds

enum class ZoomLevel(val value: Int) {
    Tele(0),
    Wide(1),
    City(2)
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class GeoJsonDataSource(
    private val apiCall: suspend (viewPort: ViewPort) -> ApiResponse<JsonElement>,
    scope: CoroutineScope
) {
    private val viewPortFlow = MutableSharedFlow<ViewPort>(
        extraBufferCapacity = 1,
        replay = 1,
    )

    private val _flow = MutableStateFlow<SourceState<String>>(SourceState.Empty)
    val flow: StateFlow<SourceState<String>> = _flow.asStateFlow()

    init {
        scope.launch {
            viewPortFlow
                .debounce(500.milliseconds).also { Log.d("ARASH", "viewPortFlow:debounce") }
                .collectLatest { viewPort ->
                    try {
                        _flow.value = SourceState.Loading
                        val result = apiCall(viewPort)
                        Log.d("APICALL", (result.data as JsonElement).toString())
                        _flow.value = SourceState.Success(result.data.toString())
                        Log.d("APICALL", GeoJsonObject.fromJson(result.data.toString()).toString())
                    } catch (e: Exception) {
                        Log.e("APICALL", e.message?: "unknown error")
                        _flow.value = SourceState.Error("${e.message} ${e.stackTraceToString()} ${e.cause?.message}")
                    }
                }
        }
    }

    fun updateViewPort(south: Double, west: Double, north: Double, east: Double, zoom: Float) {
        Log.d("ARASH", "update viewPort")
        val res = viewPortFlow.tryEmit(ViewPort(south, west, north, east, zoom))
        Log.d("ARASH", "tryEmit: $res")
    }
}
