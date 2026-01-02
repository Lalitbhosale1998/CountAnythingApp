package com.lalit.countanything.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.time.LocalTime
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lalit.countanything.R
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.ui.components.AddCounterDialog
import com.lalit.countanything.ui.components.AddFinanceTrackerDialog
import com.lalit.countanything.ui.components.BottomNavigationBar
import com.lalit.countanything.ui.models.Screen
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.ui.screens.HabitsScreen
import com.lalit.countanything.ui.screens.FinanceScreen
import com.lalit.countanything.ui.screens.HistoryScreen
import com.lalit.countanything.ui.screens.SettingsScreen
import com.lalit.countanything.ui.screens.GoalScreen
import com.lalit.countanything.ui.screens.EventsScreen
import com.lalit.countanything.ui.components.AddEventDialog
import com.lalit.countanything.ui.components.springyTouch
import com.lalit.countanything.ui.screens.CoinFlipScreen

import com.lalit.countanything.ui.screens.HiddenVaultScreen
import com.lalit.countanything.ui.screens.ManYenVisualizerScreen
import com.lalit.countanything.ui.screens.MorseCodeScreen
import com.lalit.countanything.ui.screens.NengoConverterScreen
import com.lalit.countanything.ui.screens.ToolsScreen
import com.lalit.countanything.ui.screens.SpeedDashboardScreen
import com.lalit.countanything.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun CountAnythingApp(
    settingsManager: SettingsManager,
    viewModel: MainViewModel = viewModel()
) {
    // --- State for Dialogs ---
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddCounterDialog by remember { mutableStateOf(false) }
    var showAddFinanceTrackerDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    // --- Collect State from ViewModel ---
    val displayedDate by viewModel.displayedDate.collectAsState()
    val cigaretteCount by viewModel.cigaretteCount.collectAsState()
    val history by viewModel.history.collectAsState()
    val salaryDay by viewModel.salaryDay.collectAsState()
    val monthlySalaries by viewModel.monthlySalaries.collectAsState()
    val monthlySavings by viewModel.monthlySavings.collectAsState()
    val goalTitle by viewModel.goalTitle.collectAsState()
    val goalPrice by viewModel.goalPrice.collectAsState()
    val goalAmountNeeded by viewModel.goalAmountNeeded.collectAsState()
    val totalSentToIndia by viewModel.totalSentToIndia.collectAsState()
    val genericCounters: List<Counter> by viewModel.genericCounters.collectAsState()
    val daysUntilSalary by viewModel.daysUntilSalary.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val genericCountersForDate: List<Counter> by viewModel.genericCountersForDate.collectAsState()
    val events by viewModel.events.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val isPrivacyModeEnabled by settingsManager.isPrivacyModeEnabled.collectAsState(initial = false)
    val today = LocalDate.now()
    val counterTitle = when (displayedDate) {
        today -> stringResource(R.string.counter_cigarettes_today)
        today.minusDays(1) -> stringResource(R.string.counter_cigarettes_yesterday)
        else -> stringResource(R.string.counter_cigarettes_on, displayedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    }

    // --- Pager State ---
    val pagerState = rememberPagerState(pageCount = { Screen.values().size })
    val scope = rememberCoroutineScope()
    
    // Determine Current Screen for Bottom Bar based on Pager
    val currentScreen = Screen.values().getOrElse(pagerState.currentPage) { Screen.Habits }

    // --- Back Handling ---
    // If not on the first tab (Habits), go back to Habits
    BackHandler(enabled = pagerState.currentPage != 0) {
        scope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { screen ->
                        scope.launch {
                            pagerState.animateScrollToPage(screen.ordinal)
                        }
                    },
                    onScreenLongClick = { screen ->
                        if (screen == Screen.Tools) {
                             viewModel.toggleVaultVisibility()
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentScreen == Screen.Habits || currentScreen == Screen.Finance || currentScreen == Screen.Events) {
                    FloatingActionButton(
                        onClick = { 
                            if (currentScreen == Screen.Habits) {
                                showAddCounterDialog = true 
                            } else if (currentScreen == Screen.Finance) {
                                showAddFinanceTrackerDialog = true
                            } else {
                                showAddEventDialog = true
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.springyTouch()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Counter")
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()), // Only pad bottom for the nav bar
                    userScrollEnabled = true 
                ) { page ->
                    // Map page index to Screen
                    when (Screen.values()[page]) {
                        Screen.Habits -> {
                            val fixedSexualHealthCounter = com.lalit.countanything.ui.models.Counter(
                                id = "fixed_sexual_health",
                                title = "Sexual Health & Vitality",
                                count = viewModel.sexualHealthCount.collectAsState().value,
                                type = com.lalit.countanything.ui.models.CounterType.SEXUAL_HEALTH,
                                history = viewModel.sexualHealthHistory.collectAsState().value
                            )

                            HabitsScreen(
                                counterTitle = counterTitle,
                                cigaretteCount = cigaretteCount,
                                onAddOne = { viewModel.incrementCount() },
                                onSubtractOne = { viewModel.decrementCount() },
                                onReset = { viewModel.resetCount() },
                                currencySymbol = currencySymbol,
                                genericCounters = genericCountersForDate,
                                onUpdateCounter = { id, delta -> viewModel.updateGenericCounterCount(id, delta.toFloat()) },
                                onDeleteCounter = { id -> viewModel.deleteGenericCounter(id) },
                                fixedSexualHealthCounter = fixedSexualHealthCounter,
                                onIncrementSexualHealth = { viewModel.incrementSexualHealth() },
                                onDecrementSexualHealth = { viewModel.decrementSexualHealth() },
                                selectedDate = displayedDate,
                                onPreviousDay = { viewModel.previousDay() },
                                onNextDay = { viewModel.nextDay() },
                                onResetToToday = { viewModel.resetToToday() }
                            )
                        }
                        Screen.Finance -> {
                            FinanceScreen(
                                daysUntilSalary = daysUntilSalary,
                                currencySymbol = currencySymbol,
                                salaryDay = salaryDay,
                                onSetSalaryDate = { showDatePicker = true },
                                monthlySalaries = monthlySalaries,
                                monthlySavings = monthlySavings,
                                onSaveFinancialData = { month, salary, savings ->
                                    viewModel.saveFinancialData(month, salary, savings)
                                },
                                totalSentToIndia = totalSentToIndia,
                                onAddToTotalSent = { amount ->
                                    viewModel.updateTotalSentToIndia(amount, isAddition = true)
                                },
                                onSetTotalSent = { newTotal ->
                                    viewModel.updateTotalSentToIndia(newTotal, isAddition = false)
                                },
                                settingsManager = settingsManager,
                                genericCounters = genericCounters, // Filtered internally by FinanceScreen
                                onDeleteCounter = { id -> viewModel.deleteGenericCounter(id) },
                                onUpdateBudgetHub = { id, month, salary, savings ->
                                    viewModel.updateBudgetHubData(id, month, salary, savings)
                                },
                                onUpdateCumulative = { id, amount, isAddition ->
                                    viewModel.updateCumulativeTotal(id, amount, isAddition)
                                },
                                onUpdateCountdown = { id, date ->
                                    viewModel.updateCountdownDate(id, date)
                                }
                            )
                        }
                        Screen.Goal -> {
                            GoalScreen(
                                monthlySavings = monthlySavings,
                                goalTitle = goalTitle,
                                goalPrice = goalPrice,
                                goalAmountNeeded = goalAmountNeeded,
                                currencySymbol = currencySymbol,
                                isPrivacyModeEnabled = isPrivacyModeEnabled,
                                onUpdateGoal = { title, price, amountNeeded ->
                                    viewModel.updateGoal(title, price, amountNeeded)
                                }
                            )
                        }
                        Screen.History -> {
                            HistoryScreen(
                                history = history,
                                sexualHealthHistory = viewModel.sexualHealthHistory.collectAsState().value,
                                genericCounters = genericCounters,
                                selectedDate = displayedDate,
                                onDateSelected = { newDate ->
                                    viewModel.setDisplayedDate(newDate)
                                    // Navigate back to habits to show details for that date
                                    scope.launch {
                                        pagerState.animateScrollToPage(Screen.Habits.ordinal)
                                    }
                                }
                            )
                        }
                        Screen.Settings -> {
                            SettingsScreen(
                                settingsManager = settingsManager
                            )
                        }
                        Screen.Study -> {
                            com.lalit.countanything.ui.screens.StudyScreen()
                        }
                        Screen.Events -> {
                            EventsScreen(
                                events = events,
                                onDeleteEvent = { id -> viewModel.deleteEvent(id) }

                            )
                        }
                        Screen.Tools -> {
                            var selectedTool by remember { mutableStateOf<String?>(null) }
                            
                            // Handling back press within the Tools tab
                            BackHandler(enabled = selectedTool != null) {
                                selectedTool = null
                            }

                            if (selectedTool == null) {
                                ToolsScreen(
                                    onToolSelected = { toolId -> selectedTool = toolId },
                                    isVaultVisible = viewModel.isVaultVisible.collectAsState().value
                                )
                            } else {
                                when (selectedTool) {
                                    "speed_dashboard" -> SpeedDashboardScreen(
                                        onBack = { selectedTool = null }
                                    )
                                    "coin_flip" -> CoinFlipScreen(
                                        onBack = { selectedTool = null }
                                    )
                                    "morse_code" -> MorseCodeScreen(
                                        onBack = { selectedTool = null }
                                    )
                                    "nengo_converter" -> NengoConverterScreen(
                                        onBack = { selectedTool = null }
                                    )
                                    "man_yen_visualizer" -> ManYenVisualizerScreen(
                                        onBack = { selectedTool = null }
                                    )
                                    "hidden_vault" -> HiddenVaultScreen(
                                        onBack = { selectedTool = null }
                                    )
                                    else -> Box(Modifier.fillMaxSize()) // Placeholder
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                viewModel.updateSalaryDay(newDate)
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        if (showAddCounterDialog) {
            AddCounterDialog(
                onDismiss = { showAddCounterDialog = false },
                onConfirm = { title, type ->
                    viewModel.addGenericCounter(title, type)
                    showAddCounterDialog = false
                }
            )
        }
        if (showAddFinanceTrackerDialog) {
            AddFinanceTrackerDialog(
                onDismiss = { showAddFinanceTrackerDialog = false },
                onConfirm = { title, type, date ->
                    viewModel.addGenericCounter(title, type, date)
                    showAddFinanceTrackerDialog = false
                }
            )
        }
        if (showAddEventDialog) {
            AddEventDialog(
                onDismiss = { showAddEventDialog = false },
                onConfirm = { title, date, time, isRecurring ->
                    viewModel.addEvent(title, date, time, isRecurring)
                    showAddEventDialog = false
                }
            )
        }
    }
}
