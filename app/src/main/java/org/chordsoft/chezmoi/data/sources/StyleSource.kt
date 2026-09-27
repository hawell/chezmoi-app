package org.chordsoft.chezmoi.data.sources

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.String

val mapStyles = mapOf(
    "openfreemap-liberty" to Style("openfreemap-liberty", "asset://openfreemap-liberty.json", "Noto Sans Regular"),
    "plan-ign-standard" to Style("plan-ign-standard", "asset://plan-ign-standard.json", "Open Sans Regular"),
    "simple" to Style("simple", "asset://simple.json", "Open Sans Semibold"),
    "aerienne" to Style("aerienne", "asset://aerienne.json", "Open Sans Semibold")
)

class StyleSource(private val context: Context): MutableDataSetSource<Style> {
    private val json = Json { ignoreUnknownKeys = true }
    override val flow: Flow<Style> =
        context.dataStore.data.map { prefs ->
            val stored = prefs[SettingsKeys.STYLE]
            if (stored != null) {
                try {
                    val name = json.decodeFromString<String>(stored)
                    mapStyles[name] ?: Style()
                } catch (_: Exception) {
                    Style()
                }
            } else {
                Style()
            }
        }

    override suspend fun set(newValue: Style) {
        val serialized = json.encodeToString(newValue.name)
        context.dataStore.edit {
            it[SettingsKeys.STYLE] = serialized
        }
    }
}

@Serializable
data class Style(
    val name: String = "simple",
    val file: String = "asset://simple.json",
    val textFont: String = "Open Sans Regular"
)