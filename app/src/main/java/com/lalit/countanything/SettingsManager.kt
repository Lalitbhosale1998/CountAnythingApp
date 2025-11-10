package com.lalit.countanything

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define the theme options
enum class Theme {
    LIGHT, DARK, SYSTEM
}

class SettingsManager(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_preference")
    }

    // Flow to observe theme changes
    val theme: Flow<Theme> = appContext.dataStore.data.map { preferences ->
        Theme.valueOf(preferences[THEME_KEY] ?: Theme.SYSTEM.name)
    }

    // Function to save the theme preference
    suspend fun setTheme(theme: Theme) {
        appContext.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
