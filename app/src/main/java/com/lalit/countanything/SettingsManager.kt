// In a new file: SettingsManager.ktpackage com.lalit.countanything

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define the theme options
enum class Theme {
    LIGHT, DARK, SYSTEM
}

// Create a DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
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
