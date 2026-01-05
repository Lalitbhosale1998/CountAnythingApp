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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StorageHelper {
    data class GoalData(val title: String, val price: Float, val amountNeeded: Float)
    // NEW: Event Data Class
    data class Event(
        val id: String,
        val title: String,
        val date: String, // ISO-8601 LocalDate yyyy-MM-dd
        val time: String?, // ISO-8601 LocalTime HH:mm (nullable)
        val isRecurring: Boolean
    )
    // --- Preference Keys ---
    private val DAILY_COUNTS = stringPreferencesKey("daily_cigarette_counts")
    private val DAILY_SEXUAL_HEALTH_COUNTS = stringPreferencesKey("daily_sexual_health_counts")
    private val GENERIC_COUNTERS = stringPreferencesKey("generic_counters") // NEW: List of all custom counters
    private val SALARY_DAY = intPreferencesKey("salary_day")
    // NEW: Keys for financial data
    private val MONTHLY_SALARIES = stringPreferencesKey("monthly_salaries")
    private val MONTHLY_SAVINGS = stringPreferencesKey("monthly_savings")
    // --- ADD THESE NEW KEYS ---
    private val GOAL_TITLE = stringPreferencesKey("goal_title")
    private val GOAL_PRICE = floatPreferencesKey("goal_price")
    private val GOAL_AMOUNT_NEEDED = floatPreferencesKey("goal_amount_needed")
    // NEW: Events Key
    private val EVENTS = stringPreferencesKey("events_list")
    
    // NEW: Vault Key
    private val VAULT_ENTRIES = stringPreferencesKey("vault_entries")
    
    private val GARBAGE_SCHEDULE = stringPreferencesKey("garbage_schedule")

    // --- Vault Entry Data Class ---
    data class VaultEntry(
        val id: String,
        val secretText: String
    )

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
    suspend fun saveCountForDate(context: Context, date: LocalDate, count: Float) = withContext(Dispatchers.IO) {
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
    suspend fun loadCountForDate(context: Context, date: LocalDate): Float = withContext(Dispatchers.IO) {
        val dateKey = date.format(dateFormatter)
        val jsonString = context.dataStore.data.first()[DAILY_COUNTS] ?: "{}"
        val json = JSONObject(jsonString)
        json.optDouble(dateKey, 0.0).toFloat()
    }

    /**
     * Loads all saved counts from the JSON object.
     * This is used to populate the entire calendar history.
     */
    suspend fun loadRecentCounts(context: Context): Map<String, Float> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[DAILY_COUNTS] ?: "{}"
        val json = JSONObject(jsonString)
        val countsMap = mutableMapOf<String, Float>()
        json.keys().forEach { dateKey ->
            countsMap[dateKey] = json.getDouble(dateKey).toFloat()
        }
        countsMap
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

    private val SALARY_HOLIDAYS = intPreferencesKey("salary_holidays")

    suspend fun saveSalaryHolidays(context: Context, holidays: Int) {
        context.dataStore.edit { prefs ->
            prefs[SALARY_HOLIDAYS] = holidays
        }
    }

    suspend fun loadSalaryHolidays(context: Context): Int {
        return context.dataStore.data.map { preferences ->
            preferences[SALARY_HOLIDAYS] ?: 0
        }.first()
    }

    private val SALARY_ONE_WAY_FARE = floatPreferencesKey("salary_one_way_fare")

    suspend fun saveSalaryOneWayFare(context: Context, fare: Float) {
        context.dataStore.edit { prefs ->
            prefs[SALARY_ONE_WAY_FARE] = fare
        }
    }

    suspend fun loadSalaryOneWayFare(context: Context): Float {
        return context.dataStore.data.map { preferences ->
            preferences[SALARY_ONE_WAY_FARE] ?: 0f
        }.first()
    }

    // --- NEW: Financial Data Functions ---

    /**
     * Saves the salary for a specific month.
     */
    suspend fun saveSalaryForMonth(context: Context, month: YearMonth, salary: Float) = withContext(Dispatchers.IO) {
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
    suspend fun loadAllSalaries(context: Context): Map<String, Float> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[MONTHLY_SALARIES] ?: "{}"
        val json = JSONObject(jsonString)
        val salariesMap = mutableMapOf<String, Float>()
        json.keys().forEach { monthKey ->
            salariesMap[monthKey] = json.getDouble(monthKey).toFloat()
        }
        salariesMap
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

    suspend fun saveGenericCounters(context: Context, counters: List<Counter>) = withContext(Dispatchers.IO) {
        val jsonArray = JSONArray()
        counters.forEach { counter ->
            val counterObj = JSONObject()
            counterObj.put("id", counter.id)
            counterObj.put("title", counter.title)
            counterObj.put("count", counter.count.toDouble())
            counterObj.put("type", counter.type.name)
            
            // Serialize history
            val historyJson = JSONObject()
            counter.history.forEach { (date, count) ->
                historyJson.put(date, count.toDouble())
            }
            counterObj.put("history", historyJson)
            
            // Serialize NEW Finance Fields
            counter.targetDate?.let { counterObj.put("targetDate", it) }
            
            val salariesJson = JSONObject()
            counter.monthlySalaries.forEach { (month, amount) ->
                salariesJson.put(month, amount.toDouble())
            }
            counterObj.put("monthlySalaries", salariesJson)

            val savingsJson = JSONObject()
            counter.monthlySavings.forEach { (month, amount) ->
                savingsJson.put(month, amount.toDouble())
            }
            counterObj.put("monthlySavings", savingsJson)
            
            jsonArray.put(counterObj)
        }

        context.dataStore.edit { settings ->
            settings[GENERIC_COUNTERS] = jsonArray.toString()
        }
    }

    suspend fun loadGenericCounters(context: Context): List<Counter> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[GENERIC_COUNTERS] ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val counters = mutableListOf<Counter>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getString("id")
            val title = obj.getString("title")
            val count = obj.optDouble("count", 0.0).toFloat()
            
            val typeStr = obj.optString("type", CounterType.STANDARD.name)
            val type = try {
                CounterType.valueOf(typeStr)
            } catch (e: Exception) {
                CounterType.STANDARD
            }
            
            val historyObj = obj.optJSONObject("history") ?: JSONObject()
            val historyMap = mutableMapOf<String, Float>()
            historyObj.keys().forEach { dateKey ->
                historyMap[dateKey] = historyObj.getDouble(dateKey).toFloat()
            }

            // Load NEW Finance Fields
            val targetDate = if (obj.has("targetDate")) obj.getString("targetDate") else null
            
            val salariesObj = obj.optJSONObject("monthlySalaries") ?: JSONObject()
            val salariesMap = mutableMapOf<String, Float>()
            salariesObj.keys().forEach { monthKey ->
                salariesMap[monthKey] = salariesObj.getDouble(monthKey).toFloat()
            }

            val savingsObj = obj.optJSONObject("monthlySavings") ?: JSONObject()
            val savingsMap = mutableMapOf<String, Float>()
            savingsObj.keys().forEach { monthKey ->
                savingsMap[monthKey] = savingsObj.getDouble(monthKey).toFloat()
            }

            counters.add(
                Counter(
                    id = id,
                    title = title,
                    count = count,
                    type = type,
                    history = historyMap,
                    targetDate = targetDate,
                    monthlySalaries = salariesMap,
                    monthlySavings = savingsMap
                )
            )
        }
        counters
    }

    // --- Sexual Health Permanent Storage ---

    suspend fun saveSexualHealthForDate(context: Context, date: LocalDate, count: Float) = withContext(Dispatchers.IO) {
        val dateKey = date.format(dateFormatter)
        context.dataStore.edit { settings ->
            val jsonString = settings[DAILY_SEXUAL_HEALTH_COUNTS] ?: "{}"
            val json = JSONObject(jsonString)
            json.put(dateKey, count)
            settings[DAILY_SEXUAL_HEALTH_COUNTS] = json.toString()
        }
    }

    suspend fun loadSexualHealthForDate(context: Context, date: LocalDate): Float = withContext(Dispatchers.IO) {
        val dateKey = date.format(dateFormatter)
        val jsonString = context.dataStore.data.first()[DAILY_SEXUAL_HEALTH_COUNTS] ?: "{}"
        val json = JSONObject(jsonString)
        json.optDouble(dateKey, 0.0).toFloat()
    }

    suspend fun loadRecentSexualHealthCounts(context: Context): Map<String, Float> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[DAILY_SEXUAL_HEALTH_COUNTS] ?: "{}"
        val json = JSONObject(jsonString)
        val countsMap = mutableMapOf<String, Float>()
        json.keys().forEach { dateKey ->
            countsMap[dateKey] = json.getDouble(dateKey).toFloat()
        }
        countsMap
    }

    // --- Import / Export ---
    suspend fun exportAllData(context: Context): String = withContext(Dispatchers.IO) {
        val prefs = context.dataStore.data.first()
        val exportJson = JSONObject()
        prefs.asMap().forEach { (key, value) ->
            exportJson.put(key.name, value)
        }
        exportJson.toString()
    }

    suspend fun importAllData(context: Context, jsonString: String) = withContext(Dispatchers.IO) {
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
            if (json.has("total_sent_to_india")) prefs[TOTAL_SENT_TO_INDIA] = json.getDouble("total_sent_to_india").toFloat()
            if (json.has("events_list")) prefs[EVENTS] = json.getString("events_list")
            if (json.has("vault_entries")) prefs[VAULT_ENTRIES] = json.getString("vault_entries")
        }
    }

    // --- Events Functions ---

    suspend fun saveEvents(context: Context, events: List<Event>) = withContext(Dispatchers.IO) {
        val jsonArray = JSONArray()
        events.forEach { event ->
            val obj = JSONObject()
            obj.put("id", event.id)
            obj.put("title", event.title)
            obj.put("date", event.date)
            obj.put("time", event.time ?: JSONObject.NULL)
            obj.put("isRecurring", event.isRecurring)
            jsonArray.put(obj)
        }
        context.dataStore.edit { settings ->
            settings[EVENTS] = jsonArray.toString()
        }
    }

    suspend fun loadEvents(context: Context): List<Event> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[EVENTS] ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val events = mutableListOf<Event>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getString("id")
            val title = obj.getString("title")
            val date = obj.getString("date")
            val time = if (obj.isNull("time")) null else obj.getString("time")
            val isRecurring = obj.optBoolean("isRecurring", false)
            events.add(Event(id, title, date, time, isRecurring))
        }
        events
    }


    // --- Gomi (Garbage) Schedule ---

    suspend fun saveGarbageSchedule(context: Context, schedule: Map<String, List<String>>) = withContext(Dispatchers.IO) {
        val jsonObject = JSONObject()
        schedule.forEach { (day, types) ->
            val jsonArray = JSONArray()
            types.forEach { type -> jsonArray.put(type) }
            jsonObject.put(day, jsonArray)
        }
        context.dataStore.edit { settings ->
            settings[GARBAGE_SCHEDULE] = jsonObject.toString()
        }
    }

    suspend fun loadGarbageSchedule(context: Context): Map<String, List<String>> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[GARBAGE_SCHEDULE] ?: "{}"
        val jsonObject = JSONObject(jsonString)
        val schedule = mutableMapOf<String, List<String>>()
        
        val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        days.forEach { day ->
            val types = mutableListOf<String>()
            val array = jsonObject.optJSONArray(day)
            if (array != null) {
                for (i in 0 until array.length()) {
                    types.add(array.getString(i))
                }
            }
            schedule[day] = types
        }
        schedule
    }

    // --- Vault Functions ---

    suspend fun saveVaultEntries(context: Context, entries: List<VaultEntry>) = withContext(Dispatchers.IO) {
        val jsonArray = JSONArray()
        entries.forEach { entry ->
            val obj = JSONObject()
            obj.put("id", entry.id)
            obj.put("secretText", entry.secretText)
            jsonArray.put(obj)
        }
        context.dataStore.edit { settings ->
            settings[VAULT_ENTRIES] = jsonArray.toString()
        }
    }

    suspend fun loadVaultEntries(context: Context): List<VaultEntry> = withContext(Dispatchers.IO) {
        val jsonString = context.dataStore.data.first()[VAULT_ENTRIES] ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val entries = mutableListOf<VaultEntry>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getString("id")
            val text = obj.getString("secretText")
            entries.add(VaultEntry(id, text))
        }
        entries
    }

}
