package com.lalit.countanything

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lalit.countanything.StorageHelper.dateFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object StorageHelper {

    private val DAILY_COUNTS = stringPreferencesKey("daily_cigarette_counts")
    private val SALARY_DAY = intPreferencesKey("salary_day")
    // Formatter to ensure keys in JSON are consistent
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Saves the cigarette count for a specific date into the JSON object.
     * This is now a suspend function and does not block the main thread.
     */
    suspend fun saveCountForDate(context: Context, date: LocalDate, count: Int) {
        val dateKey = date.format(dateFormatter)
        context.dataStore.edit { settings ->
            val jsonString = settings[DAILY_COUNTS] ?: "{}"
            val json = JSONObject(jsonString)
            json.put(dateKey, count)
            settings[DAILY_COUNTS] = json.toString()
        }
    }

    /**
     * Loads the cigarette count for a specific date from the JSON object.
     * This is now a suspend function. Returns 0 if no count is found.
     */
    suspend fun loadCountForDate(context: Context, date: LocalDate): Int {
        val dateKey = date.format(dateFormatter)
        val jsonString = context.dataStore.data.first()[DAILY_COUNTS] ?: "{}"
        val json = JSONObject(jsonString)
        return json.optInt(dateKey, 0)
    }

    /**
     * Loads all saved counts from the JSON object.
     * This is used to populate the entire calendar history.
     */
    suspend fun loadRecentCounts(context: Context): Map<String, Int> {
        val jsonString = context.dataStore.data.first()[DAILY_COUNTS] ?: "{}"
        val json = JSONObject(jsonString)
        val countsMap = mutableMapOf<String, Int>()
        json.keys().forEach { dateKey ->
            countsMap[dateKey] = json.getInt(dateKey)
        }
        return countsMap
    }

    /**
     * Saves the user's chosen salary day.
     * This is now a suspend function.
     */
    suspend fun saveSalaryDay(context: Context, day: Int) {
        context.dataStore.edit { prefs ->
            prefs[SALARY_DAY] = day
        }
    }

    /**
     * Loads the user's saved salary day.
     * This is now a suspend function.
     */
    suspend fun loadSalaryDay(context: Context): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[SALARY_DAY]
        }.first()
    }
}
