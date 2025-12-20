package com.lalit.countanything.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lalit.countanything.R
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.ui.components.BottomNavigationBar
import com.lalit.countanything.ui.models.Screen
import com.lalit.countanything.ui.screens.CounterScreen
import com.lalit.countanything.ui.screens.GoalScreen
import com.lalit.countanything.ui.screens.HistoryScreen
import com.lalit.countanything.ui.screens.SettingsScreen
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

    // --- Derived State ---
    val daysUntilSalary = viewModel.getDaysUntilSalary()
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
    val currentScreen = Screen.values().getOrElse(pagerState.currentPage) { Screen.Counter }

    // --- Back Handling ---
    // If not on the first tab (Counter), go back to Counter
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
                    }
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = true // Enable swiping
                ) { page ->
                    // Map page index to Screen
                    when (Screen.values()[page]) {
                        Screen.Counter -> {
                            CounterScreen(
                                counterTitle = counterTitle,
                                cigaretteCount = cigaretteCount,
                                salaryDay = salaryDay,
                                onSetSalaryDay = { newSalaryDate ->
                                    viewModel.updateSalaryDay(newSalaryDate)
                                },
                                onAddOne = { viewModel.incrementCount() },
                                onSubtractOne = { viewModel.decrementCount() },
                                onReset = { viewModel.resetCount() },
                                settingsManager = settingsManager,
                                daysUntilSalary = daysUntilSalary,
                                onSetSalaryDate = { showDatePicker = true },
                                monthlySalaries = monthlySalaries,
                                monthlySavings = monthlySavings,
                                onSaveFinancialData = { month, salary, savings ->
                                    viewModel.saveFinancialData(month, salary, savings)
                                }
                            )
                        }
                        Screen.History -> {
                            HistoryScreen(
                                history = history,
                                selectedDate = displayedDate,
                                onDateSelected = { newDate ->
                                    viewModel.setDisplayedDate(newDate)
                                    // Navigate back to counter to show details for that date
                                    scope.launch {
                                        pagerState.animateScrollToPage(Screen.Counter.ordinal)
                                    }
                                }
                            )
                        }
                        Screen.Goal -> {
                            GoalScreen(
                                monthlySavings = monthlySavings,
                                goalTitle = goalTitle,
                                goalPrice = goalPrice,
                                goalAmountNeeded = goalAmountNeeded,
                                onSaveGoal = { title, price, amountNeeded ->
                                    viewModel.updateGoal(title, price, amountNeeded)
                                },
                                totalSentToIndia = totalSentToIndia,
                                onAddToTotalSent = { amount ->
                                    viewModel.updateTotalSentToIndia(amount, isAddition = true)
                                },
                                onSetTotalSent = { newTotal ->
                                    viewModel.updateTotalSentToIndia(newTotal, isAddition = false)
                                }
                            )
                        }
                        Screen.Settings -> {
                            SettingsScreen(
                                settingsManager = settingsManager
                            )
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
    }
}
