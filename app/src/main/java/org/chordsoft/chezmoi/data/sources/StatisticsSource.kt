package org.chordsoft.chezmoi.data.sources

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class StatisticsSource(private val context: Context): MutableDataSetSource<Statistics> {
    private val json = Json { ignoreUnknownKeys = true }
    override val flow: Flow<Statistics> =
        context.dataStore.data.map { prefs ->
            val stored = prefs[SettingsKeys.STATISTICS]
            if (stored != null) {
                try {
                    val name = json.decodeFromString<String>(stored)
                    Statistics.fromValue(name)
                } catch (_: Exception) {
                    Statistics.None
                }
            } else {
                Statistics.None
            }
        }

    override suspend fun set(newValue: Statistics) {
        val serialized = json.encodeToString(newValue.value)
        context.dataStore.edit {
            it[SettingsKeys.STATISTICS] = serialized
        }
    }
}

@Serializable
enum class Statistics(val value: String) {
    None("none"),
    Poverty("poverty"),
    Population("population");

    companion object {
        fun fromValue(value: String): Statistics =
            entries.firstOrNull { it.value == value } ?: None
    }
}
