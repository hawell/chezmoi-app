package org.chordsoft.chezmoi.data.sources

import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val LAYERS = stringPreferencesKey("layers")
    val MARKERS = stringPreferencesKey("markers")
    val STYLE = stringPreferencesKey("style")
    val STATISTICS = stringPreferencesKey("Statistics")
}
