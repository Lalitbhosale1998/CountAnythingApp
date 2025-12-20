package com.lalit.countanything

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.ui.models.CounterType
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object StorageHelper {
    data class GoalData(val title: String, val price: Float, val amountNeeded: Float)
    // --- Preference Keys ---
    private val DAILY_COUNTS = stringPreferencesKey("daily_cigarette_counts")
    private val GENERIC_COUNTERS = stringPreferencesKey("generic_counters") // NEW: List of all custom counters
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

    // --- NEW: Generic Multi-Counter Functions ---

    suspend fun saveGenericCounters(context: Context, counters: List<Counter>) {
        val jsonArray = JSONArray()
        counters.forEach { counter ->
            val counterObj = JSONObject()
            counterObj.put("id", counter.id)
            counterObj.put("title", counter.title)
            counterObj.put("count", counter.count)
            counterObj.put("type", counter.type.name) // Save Type
            
            // Serialize history
            val historyJson = JSONObject()
            counter.history.forEach { (date, count) ->
                historyJson.put(date, count)
            }
            counterObj.put("history", historyJson)
            
            jsonArray.put(counterObj)
        }

        context.dataStore.edit { settings ->
            settings[GENERIC_COUNTERS] = jsonArray.toString()
        }
    }

    suspend fun loadGenericCounters(context: Context): List<Counter> {
        val jsonString = context.dataStore.data.first()[GENERIC_COUNTERS] ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val counters = mutableListOf<Counter>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getString("id")
            val title = obj.getString("title")
            val count = obj.optInt("count", 0)
            
            // Load Type safely
            val typeStr = obj.optString("type", CounterType.STANDARD.name)
            val type = try {
                CounterType.valueOf(typeStr)
            } catch (e: Exception) {
                CounterType.STANDARD
            }
            
            val historyObj = obj.optJSONObject("history") ?: JSONObject()
            val historyMap = mutableMapOf<String, Int>()
            historyObj.keys().forEach { dateKey ->
                historyMap[dateKey] = historyObj.getInt(dateKey)
            }

            counters.add(Counter(id, title, count, type, historyMap))
        }
        return counters
    }

    // --- Import / Export ---
    suspend fun exportAllData(context: Context): String {
        val prefs = context.dataStore.data.first()
        val exportJson = JSONObject()
        prefs.asMap().forEach { (key, value) ->
            exportJson.put(key.name, value)
        }
        return exportJson.toString()
    }

    suspend fun importAllData(context: Context, jsonString: String) {
        val json = JSONObject(jsonString)
        context.dataStore.edit { prefs ->
            // Clear existing data? Or merge? Let's overwrite for simple restore.
            // prefs.clear() // Optional: Clear to ensure clean state
            
            // We need to map strings back to the correct keys. 
            // Since we know our keys, we can look them up.
            if (json.has("daily_cigarette_counts")) prefs[DAILY_COUNTS] = json.getString("daily_cigarette_counts")
            if (json.has("salary_day")) prefs[SALARY_DAY] = json.getInt("salary_day")
            if (json.has("monthly_salaries")) prefs[MONTHLY_SALARIES] = json.getString("monthly_salaries")
            if (json.has("monthly_savings")) prefs[MONTHLY_SAVINGS] = json.getString("monthly_savings")
            if (json.has("goal_title")) prefs[GOAL_TITLE] = json.getString("goal_title")
            if (json.has("goal_price")) prefs[GOAL_PRICE] = json.getDouble("goal_price").toFloat()
            if (json.has("goal_amount_needed")) prefs[GOAL_AMOUNT_NEEDED] = json.getDouble("goal_amount_needed").toFloat()
            if (json.has("total_sent_to_india")) prefs[TOTAL_SENT_TO_INDIA] = json.getDouble("total_sent_to_india").toFloat()
        }
    }
}
