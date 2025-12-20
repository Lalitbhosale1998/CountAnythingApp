package com.lalit.countanything.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.Currency
import com.lalit.countanything.StorageHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val settingsManager = SettingsManager(application)

    // --- State: Date Tracking ---
    private val _displayedDate = MutableStateFlow(LocalDate.now())
    val displayedDate: StateFlow<LocalDate> = _displayedDate.asStateFlow()

    // --- State: Cigarette Counter ---
    private val _cigaretteCount = MutableStateFlow(0)
    val cigaretteCount: StateFlow<Int> = _cigaretteCount.asStateFlow()

    private val _history = MutableStateFlow<Map<String, Int>>(emptyMap())
    val history: StateFlow<Map<String, Int>> = _history.asStateFlow()

    // --- State: Salary & Finances ---
    private val _salaryDay = MutableStateFlow<LocalDate?>(null)
    val salaryDay: StateFlow<LocalDate?> = _salaryDay.asStateFlow()

    private val _monthlySalaries = MutableStateFlow<Map<String, Float>>(emptyMap())
    val monthlySalaries: StateFlow<Map<String, Float>> = _monthlySalaries.asStateFlow()

    private val _monthlySavings = MutableStateFlow<Map<String, Float>>(emptyMap())
    val monthlySavings: StateFlow<Map<String, Float>> = _monthlySavings.asStateFlow()

    // --- Reactive Calculation: Days Until Salary ---
    val daysUntilSalary: StateFlow<Long?> = _salaryDay
        .map { date -> calculateDaysUntil(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- State: Goals ---
    private val _goalTitle = MutableStateFlow("Buy Toyota GR86 SZ Manual")
    val goalTitle: StateFlow<String> = _goalTitle.asStateFlow()

    // --- CURRENCY ---
    val currencySymbol: StateFlow<String> = settingsManager.currency
        .map { it.symbol }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "¥")

    private val _goalPrice = MutableStateFlow(3195000f)
    val goalPrice: StateFlow<Float> = _goalPrice.asStateFlow()

    private val _goalAmountNeeded = MutableStateFlow(1800000f)
    val goalAmountNeeded: StateFlow<Float> = _goalAmountNeeded.asStateFlow()

    private val _totalSentToIndia = MutableStateFlow(0f)
    val totalSentToIndia: StateFlow<Float> = _totalSentToIndia.asStateFlow()

    // --- State: Generic Counters ---
    private val _genericCounters = MutableStateFlow<List<com.lalit.countanything.ui.models.Counter>>(emptyList())
    val genericCounters: StateFlow<List<com.lalit.countanything.ui.models.Counter>> = _genericCounters.asStateFlow()

    private val historyDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")


    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _history.value = StorageHelper.loadRecentCounts(context)
            _cigaretteCount.value = StorageHelper.loadCountForDate(context, _displayedDate.value)
            
            _monthlySalaries.value = StorageHelper.loadAllSalaries(context)
            _monthlySavings.value = StorageHelper.loadAllSavings(context)

            val loadedGoal = StorageHelper.loadGoal(context)
            _goalTitle.value = loadedGoal.title
            _goalPrice.value = loadedGoal.price
            _goalAmountNeeded.value = loadedGoal.amountNeeded
            
            _totalSentToIndia.value = StorageHelper.loadTotalSent(context)

            StorageHelper.loadSalaryDay(context)?.let { day ->
                val today = LocalDate.now()
                var nextDate = today.withDayOfMonth(day)
                if (today.dayOfMonth > day) {
                    nextDate = nextDate.plusMonths(1)
                }
                _salaryDay.value = nextDate
            }
            
            _genericCounters.value = StorageHelper.loadGenericCounters(context)
        }
    }

    fun setDisplayedDate(date: LocalDate) {
        _displayedDate.value = date
        viewModelScope.launch {
            _cigaretteCount.value = StorageHelper.loadCountForDate(context, date)
        }
    }

    // --- Actions: Cigarette Counter ---
    fun incrementCount() {
        val newCount = _cigaretteCount.value + 1
        updateCount(newCount)
    }

    fun decrementCount() {
        if (_cigaretteCount.value > 0) {
            val newCount = _cigaretteCount.value - 1
            updateCount(newCount)
        }
    }

    fun resetCount() {
        updateCount(0)
    }

    private fun updateCount(newCount: Int) {
        _cigaretteCount.value = newCount
        viewModelScope.launch {
            val date = _displayedDate.value
            StorageHelper.saveCountForDate(context, date, newCount)
            val dateKey = date.format(historyDateFormatter)
            _history.update { it + (dateKey to newCount) }
        }
    }

    // --- Actions: Salary ---
    fun updateSalaryDay(newDate: LocalDate) {
        _salaryDay.value = newDate
        viewModelScope.launch {
            StorageHelper.saveSalaryDay(context, newDate.dayOfMonth)
        }
    }

    fun getDaysUntilSalary(): Long? {
        return calculateDaysUntil(_salaryDay.value)
    }

    private fun calculateDaysUntil(date: LocalDate?): Long? {
        return date?.let { d ->
            val today = LocalDate.now()
            val salaryDayOfMonth = d.dayOfMonth
            var nextSalaryDate = today.withDayOfMonth(salaryDayOfMonth)
            if (today.dayOfMonth > salaryDayOfMonth) {
                nextSalaryDate = nextSalaryDate.plusMonths(1)
            }
            nextSalaryDate = when (nextSalaryDate.dayOfWeek) {
                DayOfWeek.SATURDAY -> nextSalaryDate.minusDays(2)
                DayOfWeek.SUNDAY -> nextSalaryDate.plusDays(1)
                else -> nextSalaryDate
            }
            ChronoUnit.DAYS.between(today, nextSalaryDate)
        }
    }
    
    // NO-OP here, I will modify CounterScreen directly.

    // --- Actions: Financials ---
    fun saveFinancialData(month: YearMonth, salary: Float, savings: Float) {
        viewModelScope.launch {
            val key = month.format(monthFormatter)
            StorageHelper.saveSalaryForMonth(context, month, salary)
            StorageHelper.saveSavingsForMonth(context, month, savings)
            _monthlySalaries.update { it + (key to salary) }
            _monthlySavings.update { it + (key to savings) }
        }
    }

    // --- Actions: Goals ---
    fun updateGoal(title: String, price: Float, amountNeeded: Float) {
        _goalTitle.value = title
        _goalPrice.value = price
        _goalAmountNeeded.value = amountNeeded
        viewModelScope.launch {
            StorageHelper.saveGoal(context, title, price, amountNeeded)
        }
    }

    fun updateTotalSentToIndia(amount: Float, isAddition: Boolean = false) {
        val newTotal = if(isAddition) _totalSentToIndia.value + amount else amount
        _totalSentToIndia.value = newTotal
        viewModelScope.launch {
            StorageHelper.saveTotalSent(context, newTotal)
        }
    }

    // --- Actions: Generic Counters ---

    fun addGenericCounter(title: String) {
        val newCounter = com.lalit.countanything.ui.models.Counter(title = title, type = com.lalit.countanything.ui.models.CounterType.STANDARD)
        _genericCounters.update { it + newCounter }
        saveCounters()
    }

    fun deleteGenericCounter(id: String) {
        _genericCounters.update { list -> list.filter { it.id != id } }
        saveCounters()
    }

    fun updateGenericCounterCount(id: String, delta: Int) {
        _genericCounters.update { list ->
            list.map { counter ->
                if (counter.id == id) {
                    val newCount = (counter.count + delta).coerceAtLeast(0)
                    // Update history for today
                    val todayKey = LocalDate.now().format(historyDateFormatter)
                    val newHistory = counter.history.toMutableMap()
                    newHistory[todayKey] = newCount
                    
                    counter.copy(count = newCount, history = newHistory)
                } else {
                    counter
                }
            }
        }
        saveCounters()
    }

    private fun saveCounters() {
        viewModelScope.launch {
            StorageHelper.saveGenericCounters(context, _genericCounters.value)
        }
    }
}
