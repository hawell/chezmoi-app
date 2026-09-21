package org.chordsoft.chezmoi.data.sources

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class LayersSource(private val context: Context): MutableDataSetSource<Layers> {
    private val json = Json { ignoreUnknownKeys = true }
    override val flow: Flow<Layers> =
        context.dataStore.data.map { prefs ->
            val stored = prefs[SettingsKeys.LAYERS]
            if (stored != null) {
                try {
                    json.decodeFromString<Layers>(stored)
                } catch (_: Exception) {
                    Layers()
                }
            } else {
                Layers()
            }
        }

    override suspend fun set(newValue: Layers) {
        val serialized = json.encodeToString(newValue)
        context.dataStore.edit {
            it[SettingsKeys.LAYERS] = serialized
        }
    }
}

@Serializable
data class Layers(
    val limiteAdministrative: Boolean = false,
    val cadastre: Boolean = false
)
