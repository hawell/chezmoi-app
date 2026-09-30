package org.chordsoft.chezmoi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import org.chordsoft.chezmoi.data.sources.Layers
import org.chordsoft.chezmoi.data.sources.LayersSource
import org.chordsoft.chezmoi.data.sources.Markers
import org.chordsoft.chezmoi.data.sources.MarkersSource
import org.chordsoft.chezmoi.data.sources.MutableDataSetSource
import org.chordsoft.chezmoi.data.sources.Statistics
import org.chordsoft.chezmoi.data.sources.StatisticsSource
import org.chordsoft.chezmoi.data.sources.Style
import org.chordsoft.chezmoi.data.sources.StyleSource

class SettingsViewModel(context: Context): ViewModel() {
    private val _layers: MutableDataSetSource<Layers> = LayersSource(context)
    val layers: StateFlow<Layers> =
        _layers.flow.map { it }
            .onStart { Layers() }
            .catch { Layers() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Layers()
            )

    suspend fun setLayers(layers: Layers) {
        _layers.set(layers)
    }

    private val _markers: MutableDataSetSource<Markers> = MarkersSource(context)
    val markers: StateFlow<Markers> =
        _markers.flow.map { it }
            .onStart { Markers() }
            .catch { Markers() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Markers()
            )

    suspend fun setMarkers(markers: Markers) {
        _markers.set(markers)
    }

    private val _style: MutableDataSetSource<Style> = StyleSource(context)
    val style: StateFlow<Style> =
        _style.flow.map { it }
            .onStart { Layers() }
            .catch { Layers() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Style()
            )
    suspend fun setStyle(newStyle: Style) {
        _style.set(newStyle)
    }

    private val _statistics: MutableDataSetSource<Statistics> = StatisticsSource(context)
    val statistics: StateFlow<Statistics> =
        _statistics.flow.map { it }
            .onStart { Statistics.None }
            .catch { Statistics.None }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Statistics.None
            )
    suspend fun setStatistics(newStatistics: Statistics) {
        _statistics.set(newStatistics)
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer {
                SettingsViewModel(context)
            }
        }
    }
}