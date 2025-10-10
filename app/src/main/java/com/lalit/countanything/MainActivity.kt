package com.lalit.countanything

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.lerp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import kotlin.io.path.moveTo
import kotlin.math.PI
import kotlin.math.sin


// Sealed class to represent a cell in our calendar grid
sealed class CalendarDay(val day: Int?)
class EmptyDay : CalendarDay(null)
class MonthDay(day: Int) : CalendarDay(day)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountAnyThingTheme {
                CountAnythingApp()
            }
        }
    }
}

@Composable
fun BouncyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ButtonDefaults.shape,
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
        contentPadding = PaddingValues(0.dp),
        content = content
    )
}

// Enum to represent the different screens
enum class Screen {
    Counter,
    History
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class) // Add ExperimentalAnimationApi
@Composable
fun CountAnythingApp() {
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


    // --- DATA LOADING ---
    LaunchedEffect(Unit) {
        history = StorageHelper.loadRecentCounts(context)
        cigaretteCount = StorageHelper.loadCountForDate(context, displayedDate)

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
                targetState = currentScreen,
                label = "ScreenTransition",
                modifier = Modifier.padding(innerPadding),
                transitionSpec = {
                    // --- NEW ANIMATION: Scale and Fade (Shared Axis) ---
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(220, delayMillis = 90)
                            ) togetherWith
                            fadeOut(animationSpec = tween(90)) +
                            scaleOut(targetScale = 1.1f, animationSpec = tween(90))
                }
            ) { screen ->
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (screen) {
                        Screen.Counter -> CounterScreen(
                            counterTitle = counterTitle,
                            cigaretteCount = cigaretteCount,
                            salaryDay = salaryDay,
                            onSetSalaryDay = { newSalaryDate -> // <-- FIX: Add this parameter
                                salaryDay = newSalaryDate
                                scope.launch {
                                    StorageHelper.saveSalaryDay(context, newSalaryDate.dayOfMonth)
                                }
                            },
                            onAddOne = {
                                val newCount = cigaretteCount + 1
                                // ... (rest of your code is correct)

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
                            daysUntilSalary = daysUntilSalary,
                            onSetSalaryDate = { showDatePicker = true }
                        )
                        Screen.History -> HistoryScreen(
                            history = history,
                            selectedDate = displayedDate,
                            onDateSelected = { newDate ->
                                displayedDate = newDate
                            }
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
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CounterScreen(
    counterTitle: String,
    cigaretteCount: Int,
    daysUntilSalary: Long?, // Add this parameter
    salaryDay: LocalDate?, // Add this parameter
    onAddOne: () -> Unit,
    onSubtractOne: () -> Unit,
    onReset: () -> Unit,
    onSetSalaryDate: () -> Unit,
    onSetSalaryDay: (LocalDate) -> Unit, // Add this parameter
) {
    // 1. Get the haptic feedback instance
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        // Add shape and size modifiers here
                        shape = CircleShape,
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
                        // Add shape and size modifiers here
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp) // Make the add button larger as the primary action
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
        Spacer(modifier = Modifier.height(24.dp))
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
                        text = days?.toString() ?: "No date selected",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
                // --- ADD THE WAVY PROGRESS BAR HERE ---
                Spacer(modifier = Modifier.height(16.dp))

                val salaryProgress = remember(salaryDay, daysUntilSalary) {
                    if (salaryDay == null || daysUntilSalary == null) {
                        0f
                    } else {
                        // Calculate total days in the cycle
                        val previousSalaryDate = salaryDay.minusMonths(1)
                        val totalDaysInCycle = ChronoUnit.DAYS.between(previousSalaryDate, salaryDay).toFloat()
                        if (totalDaysInCycle <= 0) return@remember 0f
                        // Calculate days passed
                        val daysPassed = totalDaysInCycle - daysUntilSalary
                        (daysPassed / totalDaysInCycle).coerceIn(0f, 1f)
                    }
                }

                WavyProgressBar(
                    progress = salaryProgress,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                BouncyButton(onClick = onSetSalaryDate) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = "Set Salary Date"
                    )
                }
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
        targetValue = 2 * PI.toFloat(),
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





@Composable
fun HistoryScreen(
    history: Map<String, Int>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        CountAnythingApp()
    }
}


