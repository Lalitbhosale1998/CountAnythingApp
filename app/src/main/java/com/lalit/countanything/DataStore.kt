package com.lalit.countanything

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Create a single DataStore instance for the entire app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
