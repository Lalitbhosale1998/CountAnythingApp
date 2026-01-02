package com.lalit.countanything.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lalit.countanything.R
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.ui.components.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.ui.models.CounterType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    daysUntilSalary: Long?,
    currencySymbol: String,
    salaryDay: LocalDate?,
    onSetSalaryDate: () -> Unit,
    monthlySalaries: Map<String, Float>,
    monthlySavings: Map<String, Float>,
    onSaveFinancialData: (YearMonth, Float, Float) -> Unit,
    totalSentToIndia: Float,
    onAddToTotalSent: (Float) -> Unit,
    onSetTotalSent: (Float) -> Unit,
    settingsManager: SettingsManager,
    genericCounters: List<Counter> = emptyList(),
    onDeleteCounter: (String) -> Unit = {},
    onUpdateBudgetHub: (String, YearMonth, Float, Float) -> Unit = { _, _, _, _ -> },
    onUpdateCumulative: (String, Float, Boolean) -> Unit = { _, _, _ -> },
    onUpdateCountdown: (String, LocalDate) -> Unit = { _, _ -> }
) {
    // --- State for the Financial Hub ---
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showSentAmountDialog by remember { mutableStateOf(false) }
    var showEditTotalSentDialog by remember { mutableStateOf(false) }

    // --- State for Custom Tracker Interaction ---
    var activeCounterId by remember { mutableStateOf<String?>(null) }
    var showCustomEditDialog by remember { mutableStateOf(false) }
    var showCustomAddDialog by remember { mutableStateOf(false) }
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var pulseTrigger by remember { mutableLongStateOf(0L) }
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    fun triggerPulse() {
        pulseTrigger = System.currentTimeMillis()
        scope.launch {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(150)
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // --- Calculations for the primary hub ---
    val monthKey = displayedMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    val currentSalary = monthlySalaries[monthKey] ?: 0f
    val currentSavings = monthlySavings[monthKey] ?: 0f
    val currentSpent = (currentSalary - currentSavings).coerceAtLeast(0f)
    val savingsProgress = if (currentSalary > 0f) (currentSavings / currentSalary).coerceIn(0f, 1f) else 0f
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val isPrivacyModeEnabled by settingsManager.isPrivacyModeEnabled.collectAsState(initial = false)

    // --- Total Savings Calculation for Header ---
    val totalSavingsFromHubs = genericCounters
        .filter { it.type == CounterType.FINANCE_BUDGET_HUB }
        .map { it.monthlySavings[monthKey] ?: 0f }
        .sum()
    val combinedSavings = currentSavings + totalSavingsFromHubs

    Column(modifier = Modifier.fillMaxSize()) {
        // Immersive gradient background or simple spacer can go here if needed, 
        // but for now, we just remove the header as requested.
        
        AnimatedColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- 1. SALARY COUNTDOWN (Now as a dedicated card) ---
            AnimatedItem(index = 0) {
                CountdownModule(
                    title = stringResource(R.string.days_until_salary),
                    targetDate = salaryDay,
                    onEditDate = onSetSalaryDate
                )
            }

            // --- 2. PRIMARY FINANCIAL HUB ---
            AnimatedItem(index = 1) {
                BudgetHubModule(
                    title = "Primary Budget",
                    displayedMonth = displayedMonth,
                    onMonthChange = { displayedMonth = it },
                    salary = currentSalary,
                    savings = currentSavings,
                    currencySymbol = currencySymbol,
                    privacyModeEnabled = isPrivacyModeEnabled,
                    onEdit = { showEditDialog = true },
                    onResetMonth = { displayedMonth = YearMonth.now() }
                )
            }

            // --- 3. INDIA REMITTANCE ---
            AnimatedItem(index = 2) {
                CumulativeTotalModule(
                    title = stringResource(R.string.money_sent_to_india),
                    total = totalSentToIndia,
                    currencySymbol = currencySymbol,
                    privacyModeEnabled = isPrivacyModeEnabled,
                    onAddAmount = { showSentAmountDialog = true },
                    onEditTotal = { showEditTotalSentDialog = true },
                    pulseTrigger = pulseTrigger
                )
            }

            // --- CUSTOM FINANCIAL TRACKERS ---
            genericCounters.filter { it.type != CounterType.STANDARD }.forEachIndexed { index, counter ->
                AnimatedItem(index = index + 3) {
                    when (counter.type) {
                        CounterType.FINANCE_COUNTDOWN -> {
                            CountdownModule(
                                title = counter.title,
                                targetDate = counter.targetDate?.let { LocalDate.parse(it) },
                                onEditDate = { 
                                    activeCounterId = counter.id
                                    showCustomDatePicker = true 
                                },
                                onDelete = { onDeleteCounter(counter.id) }
                            )
                        }
                        CounterType.FINANCE_BUDGET_HUB -> {
                            val hubSalary = counter.monthlySalaries[monthKey] ?: 0f
                            val hubSavings = counter.monthlySavings[monthKey] ?: 0f
                            BudgetHubModule(
                                title = counter.title,
                                displayedMonth = displayedMonth,
                                onMonthChange = { displayedMonth = it },
                                salary = hubSalary,
                                savings = hubSavings,
                                currencySymbol = currencySymbol,
                                privacyModeEnabled = isPrivacyModeEnabled,
                                onEdit = {
                                    activeCounterId = counter.id
                                    showCustomEditDialog = true
                                },
                                onResetMonth = { displayedMonth = YearMonth.now() },
                                onDelete = { onDeleteCounter(counter.id) }
                            )
                        }
                        CounterType.FINANCE_CUMULATIVE -> {
                            CumulativeTotalModule(
                                title = counter.title,
                                total = counter.count,
                                currencySymbol = currencySymbol,
                                privacyModeEnabled = isPrivacyModeEnabled,
                                onAddAmount = {
                                    activeCounterId = counter.id
                                    showCustomAddDialog = true
                                },
                                onEditTotal = {
                                    activeCounterId = counter.id
                                    showCustomEditDialog = true
                                },
                                onDelete = { onDeleteCounter(counter.id) },
                                pulseTrigger = if (counter.title.contains("India", ignoreCase = true)) pulseTrigger else 0L
                            )
                        }
                        else -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // --- DIALOGS ---
    if (showEditDialog) {
        FinancialEditDialog(
            displayedMonth = displayedMonth,
            initialSalary = currentSalary,
            initialSavings = currentSavings,
            onDismiss = { showEditDialog = false },
            onSave = { salary, savings ->
                onSaveFinancialData(displayedMonth, salary, savings)
                showEditDialog = false
            }
        )
    }
    if (showEditTotalSentDialog) {
        EditTotalSentDialog(
            initialAmount = totalSentToIndia,
            currencySymbol = currencySymbol,
            onDismiss = { showEditTotalSentDialog = false },
            onSave = { newTotal ->
                onSetTotalSent(newTotal)
                showEditTotalSentDialog = false
            }
        )
    }
    if (showSentAmountDialog) {
        AddAmountSentDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showSentAmountDialog = false },
            onAdd = { amount ->
                onAddToTotalSent(amount)
                triggerPulse()
                showSentAmountDialog = false
            }
        )
    }

    // --- CUSTOM TRACKER DIALOGS ---
    val activeCounter = remember(activeCounterId, genericCounters) {
        genericCounters.find { it.id == activeCounterId }
    }

    if (showCustomEditDialog && activeCounter != null) {
        when (activeCounter.type) {
            CounterType.FINANCE_BUDGET_HUB -> {
                val currentHubSalary = activeCounter.monthlySalaries[monthKey] ?: 0f
                val currentHubSavings = activeCounter.monthlySavings[monthKey] ?: 0f
                FinancialEditDialog(
                    displayedMonth = displayedMonth,
                    initialSalary = currentHubSalary,
                    initialSavings = currentHubSavings,
                    onDismiss = { showCustomEditDialog = false },
                    onSave = { salary, savings ->
                        onUpdateBudgetHub(activeCounter.id, displayedMonth, salary, savings)
                        showCustomEditDialog = false
                    }
                )
            }
            CounterType.FINANCE_CUMULATIVE -> {
                EditTotalSentDialog(
                    initialAmount = activeCounter.count,
                    currencySymbol = currencySymbol,
                    onDismiss = { showCustomEditDialog = false },
                    onSave = { newTotal ->
                        onUpdateCumulative(activeCounter.id, newTotal, false)
                        showCustomEditDialog = false
                    }
                )
            }
            else -> {}
        }
    }

    if (showCustomAddDialog && activeCounter != null) {
        AddAmountSentDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showCustomAddDialog = false },
            onAdd = { amount ->
                onUpdateCumulative(activeCounter.id, amount, true)
                if (activeCounter.type == com.lalit.countanything.ui.models.CounterType.FINANCE_CUMULATIVE && 
                    activeCounter.title.contains("India", ignoreCase = true)) {
                    triggerPulse()
                }
                showCustomAddDialog = false
            }
        )
    }

    if (showCustomDatePicker && activeCounter != null) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = activeCounter.targetDate?.let { LocalDate.parse(it).toEpochDay() * 24 * 60 * 60 * 1000 } ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showCustomDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onUpdateCountdown(activeCounter.id, LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000)))
                    }
                    showCustomDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
