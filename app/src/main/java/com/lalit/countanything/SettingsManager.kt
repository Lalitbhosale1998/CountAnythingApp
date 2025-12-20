package com.lalit.countanything

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define the settings options
enum class Theme {
    LIGHT, DARK, SYSTEM
}

enum class Currency(val symbol: String) {
    YEN("¥"), DOLLAR("$"), EURO("€"), RUPEE("₹")
}


class SettingsManager(context: Context) {

    // Use the application context to avoid memory leaks
    private val appContext = context.applicationContext



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

    // --- CURRENCY SETTINGS ---
    val currency: Flow<Currency> = appContext.dataStore.data.map { preferences ->
        Currency.valueOf(preferences[CURRENCY_KEY] ?: Currency.YEN.name)
    }

    suspend fun setCurrency(currency: Currency) {
        appContext.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency.name
        }
    }


    // Use appContext here instead of context
    val welcomeShown: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            preferences[WELCOME_SHOWN_KEY] ?: false
        }

    // Use appContext here as well
    suspend fun setWelcomeShown() {
        appContext.dataStore.edit { settings ->
            settings[WELCOME_SHOWN_KEY] = true
        }
    }

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_preference")
        val CURRENCY_KEY = stringPreferencesKey("currency_preference")
        private val WELCOME_SHOWN_KEY = booleanPreferencesKey("welcome_shown")
        private val APP_LOCK_KEY = booleanPreferencesKey("app_lock_enabled")
        private val PRIVACY_MODE_KEY = booleanPreferencesKey("privacy_mode_enabled")
    }

    // App Lock Preference
    val isAppLockEnabled: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            preferences[APP_LOCK_KEY] ?: false
        }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        appContext.dataStore.edit { settings ->
            settings[APP_LOCK_KEY] = enabled
        }
    }

    // Privacy Mode Preference
    val isPrivacyModeEnabled: Flow<Boolean> = appContext.dataStore.data
        .map { preferences ->
            preferences[PRIVACY_MODE_KEY] ?: false
        }

    suspend fun setPrivacyModeEnabled(enabled: Boolean) {
        appContext.dataStore.edit { settings ->
            settings[PRIVACY_MODE_KEY] = enabled
        }
    }
}
