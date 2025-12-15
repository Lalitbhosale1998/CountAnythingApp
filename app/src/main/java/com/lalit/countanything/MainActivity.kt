package com.lalit.countanything

import android.R.attr.maxLines
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.Edit // <-- ADD THIS IMPORT
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import java.util.UUID
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lalit.countanything.ui.theme.CountAnyThingTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Savings // Or any other suitable icon
import androidx.compose.material.icons.filled.TrackChanges // Or any other suitable icon
import androidx.compose.material3.TopAppBar


// Sealed class to represent a cell in our calendar grid
sealed class CalendarDay(val day: Int?)
class EmptyDay : CalendarDay(null)
class MonthDay(day: Int) : CalendarDay(day)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager(this)
        setContent {
            val theme by settingsManager.theme.collectAsState(initial = Theme.SYSTEM)
            val useDarkTheme = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }
            CountAnyThingTheme(darkTheme = useDarkTheme) {
                CountAnythingApp(settingsManager)
            }
        }
    }
}

@Composable
fun BouncyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "BouncyButtonScale"
    )

    Button(
        onClick = onClick,
        shape = shape,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        waitForUpOrCancellation()
                        isPressed = false
                    }
                }
            },
        contentPadding = contentPadding,
        content = content
    )
}

// Enum to represent the different screens
enum class Screen {
    Counter,
    History,
    Goals,
    Settings
}
// Sealed class to represent different types of goals
sealed class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String
) {
    // Goal Type 1: Keep daily count below a certain number
    class DailyCountGoal(
        title: String,
        val target: Int
    ) : Goal(title = title)

    // Goal Type 2: Reach a specific total count by a deadline
    class TotalCountGoal(
        title: String,
        val target: Int,
        val deadline: LocalDate
    ) : Goal(title = title)

    // Goal Type 3: Save a certain amount of money in a given month
    class SavingsGoal(
        title: String,
        val target: Float,
        val month: YearMonth
    ) : Goal(title = title)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class) // Add ExperimentalAnimationApi
@Composable
fun CountAnythingApp(settingsManager: SettingsManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State for showing/hiding the Material 3 Date Picker
    var showDatePicker by remember { mutableStateOf(false) }

    // App-specific state
    var displayedDate by remember { mutableStateOf(LocalDate.now()) }
    var cigaretteCount by remember { mutableStateOf(0) }
    var history by remember { mutableStateOf(mapOf<String, Int>()) }
    var salaryDay by remember { mutableStateOf<LocalDate?>(null) }
    var currentScreen by remember { mutableStateOf(Screen.Counter) }
    // --- NEW: Add state for financial data here at the top level ---
    var monthlySalaries by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var monthlySavings by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var goals by remember { mutableStateOf<List<Goal>>(emptyList()) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<Goal?>(null) }
    var goalTypeToCreate by remember { mutableStateOf<String?>(null) }
    val currentUi = when {
        goalToEdit != null || goalTypeToCreate != null -> "AddEditGoal"
        else -> currentScreen.name
    }

    // --- DATA LOADING ---
    LaunchedEffect(Unit) {
        history = StorageHelper.loadRecentCounts(context)
        cigaretteCount = StorageHelper.loadCountForDate(context, displayedDate)
        // Load financial data
        monthlySalaries = StorageHelper.loadAllSalaries(context)
        monthlySavings = StorageHelper.loadAllSavings(context)

        StorageHelper.loadSalaryDay(context)?.let { day ->
            val today = LocalDate.now()
            var nextDate = today.withDayOfMonth(day)
            if (today.dayOfMonth > day) {
                nextDate = nextDate.plusMonths(1)
            }
            salaryDay = nextDate
        }
    }


    // Effect to update the count whenever the displayedDate changes
    LaunchedEffect(displayedDate) {
        cigaretteCount = StorageHelper.loadCountForDate(context, displayedDate)
        // When a date is selected from the history calendar, switch back to the counter screen
        if (currentScreen == Screen.History) {
            currentScreen = Screen.Counter
        }
    }


    val daysUntilSalary = salaryDay?.let { date ->
        val today = LocalDate.now()
        val salaryDayOfMonth = date.dayOfMonth
        var nextSalaryDate = today.withDayOfMonth(salaryDayOfMonth)
        if (today.dayOfMonth > salaryDayOfMonth) {
            nextSalaryDate = nextSalaryDate.plusMonths(1)
        }
        nextSalaryDate = when (nextSalaryDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> nextSalaryDate.minusDays(1)
            DayOfWeek.SUNDAY -> nextSalaryDate.plusDays(1)
            else -> nextSalaryDate
        }
        ChronoUnit.DAYS.between(today, nextSalaryDate)
    }

    // --- DYNAMIC TITLE LOGIC ---
    val counterTitle = when (displayedDate) {
        LocalDate.now() -> "Cigarettes Smoked Today"
        LocalDate.now().minusDays(1) -> "Cigarettes Smoked Yesterday"
        else -> "Smoked on ${displayedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}"
    }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(currentScreen = currentScreen, onScreenSelected = { currentScreen = it })
            }
        ) { innerPadding ->
            // --- MODIFICATION: Use AnimatedContent for smooth screen transitions ---
            AnimatedContent(
                targetState = currentUi, // Use the new 'currentUi' state variable
                label = "MainNavigation",
                modifier = Modifier.padding(innerPadding),
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(220, delayMillis = 90)
                            ) togetherWith
                            fadeOut(animationSpec = tween(90)) +
                            scaleOut(targetScale = 1.1f, animationSpec = tween(90))
                }
            ) { uiState ->
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (uiState) {
                        "AddEditGoal" -> AddEditGoalScreen(
                            goalToEdit = goalToEdit,
                            goalTypeToCreate = goalTypeToCreate,
                            onSaveGoal = { newGoal ->
                                // This logic replaces the old goal or adds a new one
                                val existingGoals = goals.filterNot { it.id == newGoal.id }
                                goals = existingGoals + newGoal
                                // TODO: Persist changes to StorageHelper (scope.launch { ... })

                                // Navigate back by resetting the state
                                goalToEdit = null
                                goalTypeToCreate = null
                            },
                            onNavigateBack = {
                                // Navigate back by resetting the state
                                goalToEdit = null
                                goalTypeToCreate = null
                            }
                        )

                        // --- The rest of the cases now use string names ---
                        "Counter" -> CounterScreen(
                            counterTitle = counterTitle,
                            cigaretteCount = cigaretteCount,
                            salaryDay = salaryDay,
                            onSetSalaryDay = { newSalaryDate ->
                                salaryDay = newSalaryDate
                                scope.launch {
                                    StorageHelper.saveSalaryDay(context, newSalaryDate.dayOfMonth)
                                }
                            },
                            onAddOne = {
                                val newCount = cigaretteCount + 1
                                cigaretteCount = newCount
                                scope.launch {
                                    StorageHelper.saveCountForDate(context, displayedDate, newCount)
                                    history = history + (displayedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) to newCount)
                                }
                            },
                            onSubtractOne = {
                                if (cigaretteCount > 0) {
                                    val newCount = cigaretteCount - 1
                                    cigaretteCount = newCount
                                    scope.launch {
                                        StorageHelper.saveCountForDate(context, displayedDate, newCount)
                                        history = history + (displayedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) to newCount)
                                    }
                                }
                            },
                            onReset = {
                                cigaretteCount = 0
                                scope.launch {
                                    StorageHelper.saveCountForDate(context, displayedDate, 0)
                                    history = history + (displayedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) to 0)
                                }
                            },
                            settingsManager = settingsManager,
                            daysUntilSalary = daysUntilSalary,
                            onSetSalaryDate = { showDatePicker = true },
                            monthlySalaries = monthlySalaries,
                            monthlySavings = monthlySavings,
                            onSaveFinancialData = { month, salary, savings ->
                                scope.launch {
                                    val key = month.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                                    StorageHelper.saveSalaryForMonth(context, month, salary)
                                    StorageHelper.saveSavingsForMonth(context, month, savings)
                                    monthlySalaries = monthlySalaries + (key to salary)
                                    monthlySavings = monthlySavings + (key to savings)
                                }
                            }
                        )
                        "History" -> HistoryScreen(
                            history = history,
                            selectedDate = displayedDate,
                            onDateSelected = { newDate ->
                                displayedDate = newDate
                                currentScreen = Screen.Counter
                            }
                        )
                        "Settings" -> SettingsScreen(
                            settingsManager = settingsManager
                        )
                        "Goals" -> GoalsScreen(
                            goals = goals,
                            onAddGoalClicked = {
                                showAddGoalDialog = true
                            },
                            onEditGoal = { goal ->
                                // This triggers navigation to the edit screen
                                goalToEdit = goal
                            },
                            onDeleteGoal = { goal ->
                                goals = goals - goal
                                // TODO: Persist this change using StorageHelper
                                Toast.makeText(context, "${goal.title} deleted", Toast.LENGTH_SHORT).show()
                            },
                            history = history,
                            monthlySavings = monthlySavings
                        )
                    }
                }
            }

        }
        // --- NEW: Material 3 Date Picker Dialog ---
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            // Convert millis to LocalDate and save it
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                salaryDay = newDate
                                scope.launch {
                                    StorageHelper.saveSalaryDay(context, newDate.dayOfMonth)
                                }
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
        // --- Show the new Goal Type Dialog ---
        if (showAddGoalDialog) {
            ChooseGoalTypeDialog(
                onDismissRequest = { showAddGoalDialog = false },
                onGoalTypeSelected = { goalType ->
                    showAddGoalDialog = false
                    goalTypeToCreate = goalType // This will trigger navigation
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(
    goalToEdit: Goal?, // If not null, we are in "edit" mode
    goalTypeToCreate: String?, // If not null, we are in "create" mode
    onSaveGoal: (Goal) -> Unit,
    onNavigateBack: () -> Unit
) {
    val goalType = goalToEdit?.let {
        when (it) {
            is Goal.DailyCountGoal -> "daily_count"
            is Goal.SavingsGoal -> "savings"
            is Goal.TotalCountGoal -> "total_count"
        }
    } ?: goalTypeToCreate

    var title by remember { mutableStateOf(goalToEdit?.title ?: "") }
    var target by remember { mutableStateOf(
        when (val goal = goalToEdit) {
            is Goal.DailyCountGoal -> goal.target.toString()
            is Goal.SavingsGoal -> goal.target.toString()
            is Goal.TotalCountGoal -> goal.target.toString()
            else -> ""
        }
    ) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (goalToEdit == null) "Add Goal" else "Edit Goal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            when (goalType) {
                                "daily_count" -> {
                                    val newGoal = Goal.DailyCountGoal(
                                        title = title,
                                        target = target.toIntOrNull() ?: 0
                                    )
                                    onSaveGoal(newGoal)
                                }
                                "savings" -> {
                                    val newGoal = Goal.SavingsGoal(
                                        title = title,
                                        target = target.toFloatOrNull() ?: 0f,
                                        month = YearMonth.now() // Placeholder
                                    )
                                    onSaveGoal(newGoal)
                                }
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Goal Title") },
                modifier = Modifier.fillMaxWidth()
            )

            when (goalType) {
                "daily_count" -> {
                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("Daily Count Limit") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "savings" -> {
                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("Savings Target (e.g., 20000)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // TODO: Add a month picker for the savings goal
                }
            }
        }
    }
}


@Composable
fun ChooseGoalTypeDialog(
    onDismissRequest: () -> Unit,
    onGoalTypeSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Add a New Goal", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(24.dp))

                // Use a Row for side-by-side selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option 1: Daily Count Goal
                    GoalTypeOption(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.TrackChanges,
                        text = "Daily Limit",
                        onClick = { onGoalTypeSelected("daily_count") }
                    )
                    // Option 2: Savings Goal
                    GoalTypeOption(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Savings,
                        text = "Savings",
                        onClick = { onGoalTypeSelected("savings") }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalTypeOption(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }
    }
}


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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp) // Add space between cards
    ) {
        // --- CIGARETTE COUNTER CARD (EXISTING) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            // ... This card's content remains exactly the same ...
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
                            contentDescription = "Subtract One"
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
                            contentDescription = "Add One"
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
                            contentDescription = "Reset"
                        )
                    }
                }
            }
        }

        // --- NEW: DAYS UNTIL SALARY CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Days until next salary",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedContent(
                    targetState = daysUntilSalary,
                    label = "SalaryDaysAnimation"
                ) { days ->
                    Text(
                        text = days?.toString() ?: "Not Set",
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
                        contentDescription = "Set Salary Date"
                    )
                }
            }
        }
        // --- NEW: FINANCIAL HUB CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Month Selector and Edit Button
                // Header with Month Selector and Edit Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // A container for the back arrow and the "Current" button
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous Month")
                            }
                            AnimatedVisibility(visible = displayedMonth != YearMonth.now()) {
                                Button(
                                    onClick = { displayedMonth = YearMonth.now() },
                                    shape = CircleShape,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "現",
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
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Month")
                            }
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(Icons.Default.Edit, "Edit Financial Data")
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
                        Text("Amount Saved", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "¥${"%,.0f".format(currentSavings)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Amount Spent", style = MaterialTheme.typography.labelMedium)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    goals: List<Goal>,
    onAddGoalClicked: () -> Unit,
    onEditGoal: (Goal) -> Unit,
    onDeleteGoal: (Goal) -> Unit,
    // We pass this data down to calculate progress later
    history: Map<String, Int>,
    monthlySavings: Map<String, Float>
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGoalClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No goals yet. Tap '+' to add one!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        history = history,
                        monthlySavings = monthlySavings,
                        onEdit = { onEditGoal(goal) },
                        onDelete = { onDeleteGoal(goal) }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    history: Map<String, Int>,
    monthlySavings: Map<String, Float>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // This is a placeholder UI for a single goal card.
    // We'll add real progress bars and logic in a later step.
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                // Display placeholder info based on goal type
                when (goal) {
                    is Goal.DailyCountGoal -> Text("Target: < ${goal.target} per day", style = MaterialTheme.typography.bodyMedium)
                    is Goal.TotalCountGoal -> Text("Target: ${goal.target} by ${goal.deadline.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}", style = MaterialTheme.typography.bodyMedium)
                    is Goal.SavingsGoal -> Text("Target: ¥${"%,.0f".format(goal.target)} for ${goal.month.format(DateTimeFormatter.ofPattern("MMMM"))}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            // Action buttons for the goal
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Goal")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Delete Goal")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialEditDialog(
    displayedMonth: YearMonth,initialSalary: Float,
    initialSavings: Float,
    onDismiss: () -> Unit,
    onSave: (salary: Float, savings: Float) -> Unit
) {
    var salaryInput by remember { mutableStateOf(initialSalary.takeIf { it > 0 }?.toString() ?: "") }
    var savingsInput by remember { mutableStateOf(initialSavings.takeIf { it > 0 }?.toString() ?: "") }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = displayedMonth.format(monthFormatter),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        onSave(
                            salaryInput.toFloatOrNull() ?: 0f,
                            savingsInput.toFloatOrNull() ?: 0f
                        )
                    }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Salary Input
                OutlinedTextField(
                    value = salaryInput,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            salaryInput = newValue
                        }
                    },
                    label = { Text("Salary for this Month") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Savings Input
                OutlinedTextField(
                    value = savingsInput,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            savingsInput = newValue
                        }
                    },
                    label = { Text("Amount Saved this Month") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Add this new composable right before the HistoryScreen composable

// In MainActivity.kt

// In MainActivity.kt

@SuppressLint("RestrictedApi")
@Composable
fun WavyProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    waveColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    // This state will be used to animate the wave's phase, making it move
    val wavePhase by rememberInfiniteTransition(label = "WavePhase").animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "WaveAnimation"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val strokeWidth = size.height * 0.8f // Make the wave a bit thicker
        val capRadius = strokeWidth / 2
        val progressEndPx = lerp(0f, size.width, progress)

        // --- FIX IS HERE: Change the drawing order and logic ---

        // 1. Draw the wavy filled portion first
        if (progress > 0f) {
            val wavePath = Path()
            val waveAmplitude = strokeWidth * 0.3f // How high the waves are
            val waveFrequency = 0.05f // How many waves appear

            wavePath.moveTo(0f, center.y)
            for (x in 0..progressEndPx.toInt()) {
                val y = center.y + sin(x * waveFrequency + wavePhase) * waveAmplitude
                wavePath.lineTo(x.toFloat(), y)
            }
            drawPath(
                path = wavePath,
                color = waveColor,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 2. Draw the remaining track starting AFTER the wavy part ends
        if (progress < 1f) {
            val trackStartPx = (progressEndPx + capRadius).coerceAtMost(size.width - capRadius)
//            drawLine(
//                color = trackColor,
//                start = Offset(x = trackStartPx, y = center.y),
//                end = Offset(x = size.width, y = center.y),
//                strokeWidth = strokeWidth,
//                cap = StrokeCap.Round
//            )
            // Also draw the small dot at the end of the track
            drawCircle(
                color = waveColor,
                radius = capRadius * 0.3f,
                center = Offset(x = size.width - capRadius, y = center.y)
            )
        }
    }
}






// ... other imports

// Add these imports to the top of MainActivity.kt

// ... keep all other existing composables ...

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class) // Added Animation API
@Composable
fun HistoryScreen(
    history: Map<String, Int>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // This screen now ONLY shows the calendar.
        CigaretteHistoryCalendar(
            history = history,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    }
}




// In MainActivity.kt

// ... (keep all the code above this point, including HistoryScreen)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationBar(currentScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar {
        // --- COUNTER ITEM ---
        CustomNavigationBarItem(
            label = "Counter",
            isSelected = currentScreen == Screen.Counter,
            selectedIcon = Icons.Filled.CalendarToday,
            unselectedIcon = Icons.Outlined.CalendarToday,
            onClick = { onScreenSelected(Screen.Counter) }
        )

        // --- HISTORY/INSIGHTS ITEM ---
        CustomNavigationBarItem(
            label = "Insights",
            isSelected = currentScreen == Screen.History,
            selectedIcon = Icons.Filled.Analytics,
            unselectedIcon = Icons.Outlined.Analytics,
            onClick = { onScreenSelected(Screen.History) }
        )
        // --- NEW: SETTINGS ITEM ---
        CustomNavigationBarItem(
            label = "Settings",
            isSelected = currentScreen == Screen.Settings,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            onClick = { onScreenSelected(Screen.Settings) }
        )
        CustomNavigationBarItem(
            label = "Goals",
            isSelected = currentScreen == Screen.Goals,
            selectedIcon = Icons.Filled.Flag,
            unselectedIcon = Icons.Outlined.Flag,
            onClick = { onScreenSelected(Screen.Goals) }
        )

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager
) {
    val scope = rememberCoroutineScope()
    val theme by settingsManager.theme.collectAsState(initial = Theme.SYSTEM)
    val themes = Theme.values()
    val supportedLanguages = listOf("en" to "English", "ja" to "日本語")
    val context = LocalContext.current // Get the context for Toasts
    // --- INSERT THIS CODE SNIPPET ---
    // Launcher for EXPORT: Asks the user where to save a new file.
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    // This is a placeholder for your actual export logic.
                    Toast.makeText(context, "Exporting data...", Toast.LENGTH_SHORT).show()
                    // val dataToExport = StorageHelper.exportAllData(context)
                    // context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    //     outputStream.write(dataToExport.toByteArray())
                    // }
                }
            }
        }
    )

    // Launcher for IMPORT: Asks the user to pick an existing file.
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    // This is a placeholder for your actual import logic.
                    Toast.makeText(context, "Importing data...", Toast.LENGTH_SHORT).show()
                    // val jsonString = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { it.readText() }
                    // jsonString?.let { StorageHelper.importAllData(context, it) }
                }
            }
        }
    )
    // --- END OF SNIPPET ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp) // Adds space between cards
    ) {
        // --- THEME SETTINGS CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    themes.forEachIndexed { index, item ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = themes.size),
                            onClick = {
                                scope.launch { settingsManager.setTheme(item) }
                            },
                            selected = theme == item
                        ) {
                            Text(item.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
        }
        // --- NEW: LANGUAGE SETTINGS CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                val currentLang = if (currentLocale.isEmpty()) "en" else currentLocale

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    supportedLanguages.forEachIndexed { index, (langCode, langName) ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = supportedLanguages.size),
                            onClick = {
                                // Set the new app locale
                                val appLocale = LocaleListCompat.forLanguageTags(langCode)
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            },
                            selected = currentLang.startsWith(langCode)
                        ) {
                            Text(langName)
                        }
                    }
                }
            }
        }
        // --- NEW: STYLED DATA MANAGEMENT SECTION ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // --- Define the colors from your design ---
            val peachColor = Color(0xFFFDE4D6) // A light peachy color for the card backgrounds
            val brownColor = Color(0xFF6F4E37) // A coffee-brown color for text and icons

            // Pill-shaped title
            Card(
                shape = CircleShape,
//                colors = CardDefaults.cardColors(
//                    containerColor = peachColor, // Use the peach color
//                    contentColor = brownColor    // Use the brown color for the text inside
//                )
            ) {
                Text(
                    text = "Data Management",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold // Make the title bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two cards for Export and Import
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // A common set of colors for both cards
                val cardColors = CardDefaults.cardColors(
                    containerColor = peachColor,
                    contentColor = brownColor
                )

                // EXPORT CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { exportLauncher.launch("MyLog_Backup.json") },
                    shape = RoundedCornerShape(24.dp),

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Ensure column takes full width of the card
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Export Data",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Export Data", fontWeight = FontWeight.SemiBold)
                    }
                }

                // IMPORT CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { importLauncher.launch("application/json") },
                    shape = RoundedCornerShape(24.dp),

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Ensure column takes full width of the card
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Import Data",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Import Data", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // You can add more cards here for future settings like "Data Management", etc.
    }
}

@Composable
fun RowScope.CustomNavigationBarItem(
    label: String,
    isSelected: Boolean,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    // Animate the scale of the icon
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "IconScale"
    )

    // Animate the vertical offset of the icon
    val offset by animateFloatAsState(
        targetValue = if (isSelected) -5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "IconOffset"
    )

    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        icon = {
            AnimatedContent(
                targetState = isSelected,
                label = "IconAnimation",
                transitionSpec = {
                    scaleIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
                            scaleOut(animationSpec = spring(stiffness = Spring.StiffnessHigh))
                }
            ) { isCurrentlySelected ->
                Icon(
                    imageVector = if (isCurrentlySelected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationY = offset
                        }
                )
            }
        }
    )
}
@Composable
fun CigaretteHistoryCalendar(
    modifier: Modifier = Modifier,
    history: Map<String, Int>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val historyDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(selectedDate) {
        if (YearMonth.from(selectedDate) != currentMonth) {
            currentMonth = YearMonth.from(selectedDate)
        }
    }

    val calendarDays = remember(currentMonth) {
        val days = mutableListOf<CalendarDay>()
        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
        val daysInMonth = currentMonth.lengthOfMonth()
        for (i in 0 until firstDayOfMonth) {
            days.add(EmptyDay())
        }
        for (day in 1..daysInMonth) {
            days.add(MonthDay(day))
        }
        days
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarHeader(
                yearMonth = currentMonth,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DaysOfWeekHeader()
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height((6 * 48).dp)
            ) {
                items(calendarDays) { day ->
                    when (day) {
                        is EmptyDay -> {
                            Box(modifier = Modifier.aspectRatio(1f))
                        }
                        is MonthDay -> {
                            val date = currentMonth.atDay(day.day!!)
                            val historyKey = date.format(historyDateFormatter)
                            val count = history[historyKey]
                            DayCell(
                                day = day.day,
                                count = count,
                                isSelected = date == selectedDate,
                                modifier = Modifier.clickable {
                                    if (!date.isAfter(LocalDate.now())) {
                                        onDateSelected(date)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text(
            text = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    count: Int?,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val hasCount = count != null && count > 0

    // --- ANIMATION LOGIC ---

    // Animate the corner radius for the squircle effect.
    // It becomes more "square" when selected, and circular when not.
    val cornerRadius by animateFloatAsState(
        targetValue = if (isSelected) 12f else 50f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "SquircleCornerRadius"
    )

    // Animate the background color for selection, counts, or default state.
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            hasCount -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "DayCellBackgroundColor"
    )

    // Animate the main text color for better contrast against the changing background.
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            hasCount -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(300),
        label = "DayCellTextColor"
    )

    // Animate the count text color for better contrast.
    val countColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        animationSpec = tween(300),
        label = "DayCellCountColor"
    )


    // --- CELL UI ---

    val cellModifier = modifier
        .aspectRatio(1f)
        // Clip the cell using our custom shape with the animated corner radius.
        // We multiply by density to convert the value correctly for the Shape class.
        .clip(SquircleShape(cornerRadius * LocalDensity.current.density))
        // Apply the animated background color.
        .background(backgroundColor)

    Box(
        modifier = cellModifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (hasCount || isSelected) FontWeight.Bold else FontWeight.Normal,
                // Use the animated text color.
                color = textColor
            )
            if (hasCount) {
                Text(
                    text = count.toString(),
                    fontSize = 10.sp,
                    // Use the animated color for the count text.
                    color = countColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// A custom "squircle" shape that can be animated
class SquircleShape(private val cornerRadius: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rounded(
            RoundRect(
                rect = Rect(0f, 0f, size.width, size.height),
                cornerRadius = CornerRadius(cornerRadius)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CountAnythingPreview() {
    CountAnyThingTheme {
        //CountAnythingApp()
    }
}
