package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Precise Era Definitions
enum class JapanEra(
    val kanji: String, 
    val english: String, 
    val start: LocalDate, 
    val end: LocalDate?
) {
    REIWA("令和", "Reiwa", LocalDate.of(2019, 5, 1), null),
    HEISEI("平成", "Heisei", LocalDate.of(1989, 1, 8), LocalDate.of(2019, 4, 30)),
    SHOWA("昭和", "Showa", LocalDate.of(1926, 12, 25), LocalDate.of(1989, 1, 7)),
    TAISHO("大正", "Taisho", LocalDate.of(1912, 7, 30), LocalDate.of(1926, 12, 25)),
    MEIJI("明治", "Meiji", LocalDate.of(1868, 1, 25), LocalDate.of(1912, 7, 29)); 
    // Meiji start is complex due to lunar calendar, but 1868-01-25 is widely accepted start of Meiji 1, 
    // though Gregorian adoption was Meiji 6 (1873). For simple converter we stick to this.

    companion object {
        fun fromDate(date: LocalDate): JapanEra? {
            return values().firstOrNull { era ->
                (date.isEqual(era.start) || date.isAfter(era.start)) &&
                (era.end == null || date.isEqual(era.end) || date.isBefore(era.end))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NengoConverterScreen(
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    // State
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Logic
    val eraObj = JapanEra.fromDate(selectedDate)
    val eraYearDisplay = if (eraObj != null) {
        val y = selectedDate.year - eraObj.start.year + 1
        if (y == 1) "元年 (1)" else y.toString()
    } else {
        "?"
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Nengo Converter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. RESULT TICKET
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "SELECTED DATE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                    
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    if (eraObj != null) {
                        Spacer(modifier = Modifier.height(32.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            "JAPANESE ERA",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                            letterSpacing = 2.sp
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = eraObj.kanji,
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$eraYearDisplay 年",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                         Text(
                            text = "${selectedDate.monthValue}月 ${selectedDate.dayOfMonth}日", // Month Day
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${eraObj.english} $eraYearDisplay",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    } else {
                         Spacer(modifier = Modifier.height(32.dp))
                         Text("Pre-Meiji or Future", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. INPUT BUTTON
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.EditCalendar, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Date", style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3. QUICK JUMPS
            Text("Quick Jumps", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            val quickDates = listOf(
                "Today" to LocalDate.now(),
                "Reiwa Start" to LocalDate.of(2019, 5, 1),
                "Heisei Start" to LocalDate.of(1989, 1, 8),
                "Showa Start" to LocalDate.of(1926, 12, 25)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickDates.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { (label, date) ->
                            SuggestionChip(
                                onClick = { selectedDate = date },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f),
                                icon = { Icon(Icons.Default.History, null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
