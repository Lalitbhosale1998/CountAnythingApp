package com.lalit.countanything.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lalit.countanything.SettingsManager
import com.lalit.countanything.ui.components.BouncyButton
import com.lalit.countanything.ui.components.FinancialEditDialog
import com.lalit.countanything.ui.components.WavyProgressBar
import com.lalit.countanything.ui.components.springyTouch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import androidx.compose.ui.res.stringResource
import com.lalit.countanything.R

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    counterTitle: String,
    cigaretteCount: Int,
    onAddOne: () -> Unit,
    onSubtractOne: () -> Unit,
    onReset: () -> Unit,
    settingsManager: SettingsManager,
    // Financial parameters
    daysUntilSalary: Long?,
    salaryDay: LocalDate?,
    onSetSalaryDate: () -> Unit,
    onSetSalaryDay: (LocalDate) -> Unit,
    monthlySalaries: Map<String, Float>,
    monthlySavings: Map<String, Float>,
    onSaveFinancialData: (YearMonth, Float, Float) -> Unit
) {
    // --- Haptics and Scope ---
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // --- State for the Financial Hub ---
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    var showEditDialog by remember { mutableStateOf(false) }

    // --- Calculations for the displayed month ---
    val monthKey = displayedMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    val currentSalary = monthlySalaries[monthKey] ?: 0f
    val currentSavings = monthlySavings[monthKey] ?: 0f
    val currentSpent = (currentSalary - currentSavings).coerceAtLeast(0f)
    val savingsProgress = if (currentSalary > 0f) (currentSavings / currentSalary).coerceIn(0f, 1f) else 0f
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp), // KEEP this for horizontal/top padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- CIGARETTE COUNTER CARD (EXISTING) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .springyTouch(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = counterTitle, // Use dynamic title
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedContent(
                    targetState = cigaretteCount,
                    label = "CigaretteCountAnimation",
                    transitionSpec = {
                        // Determine if the count is increasing or decreasing for directional animation
                        if (targetState > initialState) {
                            // Animation for INCREASING count: slides in from bottom
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut()
                        } else {
                            // Animation for DECREASING count: slides in from top
                            slideInVertically { height -> -height } + fadeIn() togetherWith
                                    slideOutVertically { height -> height } + fadeOut()
                        }.using(
                            // This SizeTransform applies the bouncy "overshoot" spring effect
                            SizeTransform { _, _ ->
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            }
                        )
                    }
                ) { count ->
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Align buttons nicely
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // --- SUBTRACT BUTTON WITH ICON ---
                    BouncyButton(onClick = {
                        onSubtractOne()
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.subtract_one)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // --- ADD BUTTON WITH ICON ---
                    BouncyButton(onClick = {
                        onAddOne()
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_one)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // --- RESET BUTTON WITH ICON ---
                    BouncyButton(onClick = {
                        onReset()
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset)
                        )
                    }
                }
            }
        }

        // --- NEW: DAYS UNTIL SALARY CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .springyTouch(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.days_until_salary),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedContent(
                    targetState = daysUntilSalary,
                    label = "SalaryDaysAnimation"
                ) { days ->
                    Text(
                        text = days?.toString() ?: stringResource(R.string.not_set),
                        style = MaterialTheme.typography.displayLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                val salaryProgress = remember(salaryDay, daysUntilSalary) {
                    if (salaryDay == null || daysUntilSalary == null) 0f
                    else {
                        val previousSalaryDate = salaryDay.minusMonths(1)
                        val totalDaysInCycle = ChronoUnit.DAYS.between(previousSalaryDate, salaryDay).toFloat()
                        if (totalDaysInCycle <= 0) return@remember 0f
                        val daysPassed = totalDaysInCycle - daysUntilSalary
                        (daysPassed / totalDaysInCycle).coerceIn(0f, 1f)
                    }
                }

                WavyProgressBar(
                    progress = salaryProgress,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                BouncyButton(
                    onClick = onSetSalaryDate,
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = stringResource(R.string.set_salary_date)
                    )
                }
            }
        }
        // --- NEW: FINANCIAL HUB CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .springyTouch(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Month Selector and Edit Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // A container for the back arrow and the "Current" button
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, stringResource(R.string.prev_month))
                            }
                            AnimatedVisibility(visible = displayedMonth != YearMonth.now()) {
                                Button(
                                    onClick = { displayedMonth = YearMonth.now() },
                                    shape = CircleShape,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.current),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }


                    // The animated month title
                    AnimatedContent(
                        targetState = displayedMonth,
                        label = "MonthTitle",
                        modifier = Modifier.weight(1.5f) // Give it a bit more space
                    ) { month ->
                        Text(
                            text = month.format(monthFormatter),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // A container for the next arrow and the edit button
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        Row {
                            IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, stringResource(R.string.next_month))
                            }
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(Icons.Default.Edit, stringResource(R.string.edit_financial_data))
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Savings Progress and Key Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Circular Progress
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { savingsProgress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 8.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "${(savingsProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Saved and Spent amounts
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.amount_saved), style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "¥${"%,.0f".format(currentSavings)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.amount_spent), style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "¥${"%,.0f".format(currentSpent)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Padding to avoid navigation bar overlap

    } // End of main Column

    // --- Show the Dialog when needed ---
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
}
