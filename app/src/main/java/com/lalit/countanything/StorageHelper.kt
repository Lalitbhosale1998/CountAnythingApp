package com.lalit.countanything

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object StorageHelper {

    // --- Preference Keys ---
    private val DAILY_COUNTS = stringPreferencesKey("daily_cigarette_counts")
    private val SALARY_DAY = intPreferencesKey("salary_day")
    // NEW: Keys for financial data
    private val MONTHLY_SALARIES = stringPreferencesKey("monthly_salaries")
    private val MONTHLY_SAVINGS = stringPreferencesKey("monthly_savings")

    // --- Formatters ---
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    // NEW: Formatter for month keys
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")


    // --- Cigarette Count Functions ---

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

    // --- Salary Day Functions ---

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

    // --- NEW: Financial Data Functions ---

    /**
     * Saves the salary for a specific month.
     */
    suspend fun saveSalaryForMonth(context: Context, month: YearMonth, salary: Float) {
        val monthKey = month.format(monthFormatter)
        context.dataStore.edit { settings ->
            val jsonString = settings[MONTHLY_SALARIES] ?: "{}"
            val json = JSONObject(jsonString)
            json.put(monthKey, salary.toDouble()) // JSON handles doubles
            settings[MONTHLY_SALARIES] = json.toString()
        }
    }

    /**
     * Loads all stored monthly salaries.
     */
    suspend fun loadAllSalaries(context: Context): Map<String, Float> {
        val jsonString = context.dataStore.data.first()[MONTHLY_SALARIES] ?: "{}"
        val json = JSONObject(jsonString)
        val salariesMap = mutableMapOf<String, Float>()
        json.keys().forEach { monthKey ->
            salariesMap[monthKey] = json.getDouble(monthKey).toFloat()
        }
        return salariesMap
    }

    /**
     * Saves the savings amount for a specific month.
     */
    suspend fun saveSavingsForMonth(context: Context, month: YearMonth, savings: Float) {
        val monthKey = month.format(monthFormatter)
        context.dataStore.edit { settings ->
            val jsonString = settings[MONTHLY_SAVINGS] ?: "{}"
            val json = JSONObject(jsonString)
            json.put(monthKey, savings.toDouble())
            settings[MONTHLY_SAVINGS] = json.toString()
        }
    }

    /**
     * Loads all stored monthly savings.
     */
    suspend fun loadAllSavings(context: Context): Map<String, Float> {
        val jsonString = context.dataStore.data.first()[MONTHLY_SAVINGS] ?: "{}"
        val json = JSONObject(jsonString)
        val savingsMap = mutableMapOf<String, Float>()
        json.keys().forEach { monthKey ->
            savingsMap[monthKey] = json.getDouble(monthKey).toFloat()
        }
        return savingsMap
    }
}
