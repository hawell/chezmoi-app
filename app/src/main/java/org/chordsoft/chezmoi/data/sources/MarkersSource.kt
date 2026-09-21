package org.chordsoft.chezmoi.data.sources

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MarkersSource(private val context: Context): MutableDataSetSource<Markers> {
    private val json = Json { ignoreUnknownKeys = true }
    override val flow: Flow<Markers> =
        context.dataStore.data.map { prefs ->
            val stored = prefs[SettingsKeys.MARKERS]
            if (stored != null) {
                try {
                    json.decodeFromString<Markers>(stored)
                } catch (_: Exception) {
                    Markers()
                }
            } else {
                Markers()
            }
        }

    override suspend fun set(newValue: Markers) {
        val serialized = json.encodeToString(newValue)
        context.dataStore.edit {
            it[SettingsKeys.MARKERS] = serialized
        }
    }
}

@Serializable
data class Markers(
    val rpls: Boolean = false
)