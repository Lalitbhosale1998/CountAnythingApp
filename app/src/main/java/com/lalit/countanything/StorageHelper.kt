package com.lalit.countanything

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object StorageHelper {
    data class GoalData(val title: String, val price: Float, val amountNeeded: Float)
    // --- Preference Keys ---
    private val DAILY_COUNTS = stringPreferencesKey("daily_cigarette_counts")
    private val SALARY_DAY = intPreferencesKey("salary_day")
    // NEW: Keys for financial data
    private val MONTHLY_SALARIES = stringPreferencesKey("monthly_salaries")
    private val MONTHLY_SAVINGS = stringPreferencesKey("monthly_savings")
    // --- ADD THESE NEW KEYS ---
    private val GOAL_TITLE = stringPreferencesKey("goal_title")
    private val GOAL_PRICE = floatPreferencesKey("goal_price")
    private val GOAL_AMOUNT_NEEDED = floatPreferencesKey("goal_amount_needed")

    // --- Formatters ---
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    // NEW: Formatter for month keys
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    private val TOTAL_SENT_TO_INDIA = floatPreferencesKey("total_sent_to_india")
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
    suspend fun saveGoal(context: Context, title: String, price: Float, amountNeeded: Float) {
        context.dataStore.edit { settings ->
            settings[GOAL_TITLE] = title
            settings[GOAL_PRICE] = price
            settings[GOAL_AMOUNT_NEEDED] = amountNeeded
        }
    }

    /**
     * Loads the user's goal data from DataStore.
     */
    suspend fun loadGoal(context: Context): GoalData {
        val prefs = context.dataStore.data.first()
        val title = prefs[GOAL_TITLE] ?: "Buy Toyota GR86 SZ Manual"
        val price = prefs[GOAL_PRICE] ?: 3195000f
        val amountNeeded = prefs[GOAL_AMOUNT_NEEDED] ?: 1800000f
        return GoalData(title, price, amountNeeded)
    }
    // 2. Add the new load and save functions
    suspend fun saveTotalSent(context: Context, totalAmount: Float) {
        context.dataStore.edit { settings ->
            settings[TOTAL_SENT_TO_INDIA] = totalAmount
        }
    }

    suspend fun loadTotalSent(context: Context): Float {
        val prefs = context.dataStore.data.first()
        return prefs[TOTAL_SENT_TO_INDIA] ?: 0f
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
