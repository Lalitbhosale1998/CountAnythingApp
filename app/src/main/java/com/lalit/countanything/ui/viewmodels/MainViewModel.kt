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
    private val _isVaultVisible = MutableStateFlow(false)
    val isVaultVisible: StateFlow<Boolean> = _isVaultVisible.asStateFlow()

    fun toggleVaultVisibility() {
        _isVaultVisible.value = !_isVaultVisible.value
    }

    private val _displayedDate = MutableStateFlow(LocalDate.now())
    val displayedDate: StateFlow<LocalDate> = _displayedDate.asStateFlow()

    // --- State: Cigarette Counter ---
    private val _cigaretteCount = MutableStateFlow(0f)
    val cigaretteCount: StateFlow<Float> = _cigaretteCount.asStateFlow()

    private val _history = MutableStateFlow<Map<String, Float>>(emptyMap())
    val history: StateFlow<Map<String, Float>> = _history.asStateFlow()

    // --- State: Sexual Health (Fixed) ---
    private val _sexualHealthCount = MutableStateFlow(0f)
    val sexualHealthCount: StateFlow<Float> = _sexualHealthCount.asStateFlow()

    private val _sexualHealthHistory = MutableStateFlow<Map<String, Float>>(emptyMap())
    val sexualHealthHistory: StateFlow<Map<String, Float>> = _sexualHealthHistory.asStateFlow()

    // --- State: Salary & Finances ---
    private val _salaryDay = MutableStateFlow<LocalDate?>(null)
    val salaryDay: StateFlow<LocalDate?> = _salaryDay.asStateFlow()

    private val _monthlySalaries = MutableStateFlow<Map<String, Float>>(emptyMap())
    val monthlySalaries: StateFlow<Map<String, Float>> = _monthlySalaries.asStateFlow()

    private val _monthlySavings = MutableStateFlow<Map<String, Float>>(emptyMap())
    val monthlySavings: StateFlow<Map<String, Float>> = _monthlySavings.asStateFlow()

    // --- Reactive Calculation: Days Until Salary ---
    // --- Reactive Calculation: Days Until Salary ---
    private val _salaryHolidays = MutableStateFlow(0)
    val salaryHolidays: StateFlow<Int> = _salaryHolidays.asStateFlow()

    val daysUntilSalary: StateFlow<Long?> = _salaryDay
        .map { date -> calculateDaysUntil(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val workingDaysUntilSalary: StateFlow<Long?> = combine(_salaryDay, _salaryHolidays) { date, holidays ->
        calculateWorkingDaysUntil(date, holidays)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _salaryOneWayFare = MutableStateFlow(0f)
    val salaryOneWayFare: StateFlow<Float> = _salaryOneWayFare.asStateFlow()

    val projectedCommuteCost: StateFlow<Float> = combine(workingDaysUntilSalary, _salaryOneWayFare) { workingDays, fare ->
        if (workingDays != null) {
            (workingDays * (fare * 2)) // One way * 2 * working days
        } else {
            0f
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // --- State: Goals ---
    private val _goalTitle = MutableStateFlow("Buy Toyota GR86 SZ Manual")
    val goalTitle: StateFlow<String> = _goalTitle.asStateFlow()

    // --- CURRENCY ---
    val currencySymbol: StateFlow<String> = settingsManager.currency
        .map { it.symbol }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "¥")

    private val _goalPrice = MutableStateFlow(3195000f)
    val goalPrice: StateFlow<Float> = _goalPrice.asStateFlow()

    // --- USER NAME ---
    val userName: StateFlow<String> = settingsManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun updateUserName(name: String) {
        viewModelScope.launch {
            settingsManager.setUserName(name)
        }
    }

    private val _goalAmountNeeded = MutableStateFlow(1800000f)
    val goalAmountNeeded: StateFlow<Float> = _goalAmountNeeded.asStateFlow()

    private val _totalSentToIndia = MutableStateFlow(0f)
    val totalSentToIndia: StateFlow<Float> = _totalSentToIndia.asStateFlow()

    // --- State: Events ---
    private val _events = MutableStateFlow<List<StorageHelper.Event>>(emptyList())
    val events: StateFlow<List<StorageHelper.Event>> = _events.asStateFlow()

    // --- State: Generic Counters ---
    private val _genericCounters = MutableStateFlow<List<com.lalit.countanything.ui.models.Counter>>(emptyList())
    val genericCounters: StateFlow<List<com.lalit.countanything.ui.models.Counter>> = _genericCounters.asStateFlow()

    // --- Reactive Calculation: Generic Counters for the Selected Date ---
    val genericCountersForDate: StateFlow<List<com.lalit.countanything.ui.models.Counter>> = combine(
        _genericCounters,
        _displayedDate
    ) { counters, date ->
        val dateKey = date.format(historyDateFormatter)
        counters.map { counter ->
            if (counter.type == com.lalit.countanything.ui.models.CounterType.STANDARD) {
                val historicalCount = counter.history[dateKey] ?: 0f
                counter.copy(count = historicalCount)
            } else {
                counter
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
                if (today.dayOfMonth > day) {
                    nextDate = nextDate.plusMonths(1)
                }
                _salaryDay.value = nextDate
            }
            _salaryHolidays.value = StorageHelper.loadSalaryHolidays(context)
            _salaryOneWayFare.value = StorageHelper.loadSalaryOneWayFare(context)
            
            _genericCounters.value = StorageHelper.loadGenericCounters(context)

            _sexualHealthHistory.value = StorageHelper.loadRecentSexualHealthCounts(context)
            _sexualHealthCount.value = StorageHelper.loadSexualHealthForDate(context, _displayedDate.value)
            
            _events.value = StorageHelper.loadEvents(context)
            _vaultEntries.value = StorageHelper.loadVaultEntries(context)
        }
    }

    fun setDisplayedDate(date: LocalDate) {
        _displayedDate.value = date
        viewModelScope.launch {
            _cigaretteCount.value = StorageHelper.loadCountForDate(context, date)
            _sexualHealthCount.value = StorageHelper.loadSexualHealthForDate(context, date)
        }
    }

    fun previousDay() {
        setDisplayedDate(_displayedDate.value.minusDays(1))
    }

    fun nextDay() {
        setDisplayedDate(_displayedDate.value.plusDays(1))
    }

    fun resetToToday() {
        setDisplayedDate(LocalDate.now())
    }

    // --- Actions: Cigarette Counter ---
    fun incrementCount() {
        val newCount = _cigaretteCount.value + 1f
        updateCount(newCount)
    }

    fun decrementCount() {
        if (_cigaretteCount.value > 0) {
            val newCount = _cigaretteCount.value - 1f
            updateCount(newCount)
        }
    }

    fun resetCount() {
        updateCount(0f)
    }

    private fun updateCount(newCount: Float) {
        _cigaretteCount.value = newCount
        viewModelScope.launch {
            val date = _displayedDate.value
            StorageHelper.saveCountForDate(context, date, newCount)
            val dateKey = date.format(historyDateFormatter)
            _history.update { it + (dateKey to newCount) }
        }
    }

    // --- Actions: Sexual Health (Fixed) ---
    fun incrementSexualHealth() {
        val newCount = _sexualHealthCount.value + 1f
        updateSexualHealthCount(newCount)
    }

    fun decrementSexualHealth() {
        if (_sexualHealthCount.value > 0) {
            val newCount = _sexualHealthCount.value - 1f
            updateSexualHealthCount(newCount)
        }
    }

    private fun updateSexualHealthCount(newCount: Float) {
        _sexualHealthCount.value = newCount
        viewModelScope.launch {
            val date = _displayedDate.value
            StorageHelper.saveSexualHealthForDate(context, date, newCount)
            val dateKey = date.format(historyDateFormatter)
            _sexualHealthHistory.update { it + (dateKey to newCount) }
        }
    }

    // --- Actions: Salary ---
    fun updateSalaryDay(newDate: LocalDate, holidays: Int, oneWayFare: Float) {
        _salaryDay.value = newDate
        _salaryHolidays.value = holidays
        _salaryOneWayFare.value = oneWayFare
        viewModelScope.launch {
            StorageHelper.saveSalaryDay(context, newDate.dayOfMonth)
            StorageHelper.saveSalaryHolidays(context, holidays)
            StorageHelper.saveSalaryOneWayFare(context, oneWayFare)
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
            // For simple calendar days, we just want the distance
            ChronoUnit.DAYS.between(today, nextSalaryDate)
        }
    }

    private fun calculateWorkingDaysUntil(date: LocalDate?, holidays: Int): Long? {
        return date?.let { d ->
            val today = LocalDate.now()
            val salaryDayOfMonth = d.dayOfMonth
            var nextSalaryDate = today.withDayOfMonth(salaryDayOfMonth)
            if (today.dayOfMonth > salaryDayOfMonth) {
                nextSalaryDate = nextSalaryDate.plusMonths(1)
            }
            
            // Calculate total days excluding weekends
            var workingDays = 0L
            var currentDate = today.plusDays(1) // Start counting from tomorrow? or today? usually typically from "now"
            // Let's count days in range (today, nextSalaryDate]
            while (!currentDate.isAfter(nextSalaryDate)) {
                 if (currentDate.dayOfWeek != DayOfWeek.SATURDAY && currentDate.dayOfWeek != DayOfWeek.SUNDAY) {
                     workingDays++
                 }
                 currentDate = currentDate.plusDays(1)
            }
            
            // Subtract custom holidays
            (workingDays - holidays).coerceAtLeast(0)
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

    fun addGenericCounter(
        title: String,
        type: com.lalit.countanything.ui.models.CounterType,
        targetDate: String? = null
    ) {
        val newCounter = com.lalit.countanything.ui.models.Counter(
            title = title,
            type = type,
            targetDate = targetDate
        )
        _genericCounters.update { it + newCounter }
        saveCounters()
    }

    fun deleteGenericCounter(id: String) {
        _genericCounters.update { list -> list.filter { it.id != id } }
        saveCounters()
    }

    fun updateGenericCounterCount(id: String, delta: Float) {
        _genericCounters.update { list ->
            list.map { counter ->
                if (counter.id == id) {
                    val dateKey = _displayedDate.value.format(historyDateFormatter)
                    val currentHistoricalCount = counter.history[dateKey] ?: 0f
                    val newCount = (currentHistoricalCount + delta).coerceAtLeast(0f)
                    
                    val newHistory = counter.history.toMutableMap()
                    newHistory[dateKey] = newCount
                    
                    counter.copy(count = newCount, history = newHistory)
                } else {
                    counter
                }
            }
        }
        saveCounters()
    }

    fun updateBudgetHubData(id: String, month: YearMonth, salary: Float, savings: Float) {
        val monthKey = month.format(monthFormatter)
        _genericCounters.update { list ->
            list.map { counter ->
                if (counter.id == id) {
                    val newSalaries = counter.monthlySalaries.toMutableMap()
                    newSalaries[monthKey] = salary
                    val newSavings = counter.monthlySavings.toMutableMap()
                    newSavings[monthKey] = savings
                    counter.copy(monthlySalaries = newSalaries, monthlySavings = newSavings)
                } else {
                    counter
                }
            }
        }
        saveCounters()
    }

    fun updateCumulativeTotal(id: String, amount: Float, isAddition: Boolean) {
        _genericCounters.update { list ->
            list.map { counter ->
                if (counter.id == id) {
                    val newTotal = if (isAddition) counter.count + amount else amount
                    counter.copy(count = newTotal)
                } else {
                    counter
                }
            }
        }
        saveCounters()
    }

    fun updateCountdownDate(id: String, newDate: LocalDate) {
        val dateStr = newDate.format(historyDateFormatter)
        _genericCounters.update { list ->
            list.map { counter ->
                if (counter.id == id) {
                    counter.copy(targetDate = dateStr)
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

    // --- Actions: Events ---
    fun addEvent(title: String, date: LocalDate, time: java.time.LocalTime?, isRecurring: Boolean) {
        val newEvent = StorageHelper.Event(
            id = java.util.UUID.randomUUID().toString(),
            title = title,
            date = date.format(historyDateFormatter),
            time = time?.format(DateTimeFormatter.ISO_LOCAL_TIME),
            isRecurring = isRecurring
        )
        _events.update { it + newEvent }
        saveEvents()
    }

    fun deleteEvent(id: String) {
        _events.update { list -> list.filter { it.id != id } }
        saveEvents()
    }

    private fun saveEvents() {
        viewModelScope.launch {
            StorageHelper.saveEvents(context, _events.value)
        }
    }
    // --- Actions: Vault ---

    private val _vaultEntries = MutableStateFlow<List<StorageHelper.VaultEntry>>(emptyList())
    val vaultEntries: StateFlow<List<StorageHelper.VaultEntry>> = _vaultEntries.asStateFlow()

    fun addVaultEntry(text: String) {
        val newEntry = StorageHelper.VaultEntry(
            id = java.util.UUID.randomUUID().toString(),
            secretText = text
        )
        _vaultEntries.update { it + newEntry }
        saveVaultEntries()
    }

    fun updateVaultEntry(id: String, newText: String) {
        _vaultEntries.update { list ->
            list.map { if (it.id == id) it.copy(secretText = newText) else it }
        }
        saveVaultEntries()
    }

    fun deleteVaultEntry(id: String) {
        _vaultEntries.update { list -> list.filter { it.id != id } }
        saveVaultEntries()
    }

    private fun saveVaultEntries() {
        viewModelScope.launch {
            StorageHelper.saveVaultEntries(context, _vaultEntries.value)
        }
    }
}
