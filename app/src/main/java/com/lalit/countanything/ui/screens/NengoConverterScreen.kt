package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.components.springyTouch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Precise Era Definitions (Preserved)
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
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Cyberpunk Theme Colors
    val neonPink = Color(0xFFFF4081)
    val bgDark = Color(0xFF101010)
    val panelBg = Color(0xFF1E1E1E)

    // Logic (Preserved)
    val eraObj = JapanEra.fromDate(selectedDate)
    val eraYearDisplay = if (eraObj != null) {
        val y = selectedDate.year - eraObj.start.year + 1
        if (y == 1) "元年" else y.toString()
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
                }) { Text("CONFIRM", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCEL", fontFamily = FontFamily.Monospace) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "TIME_WARP // MAINFRAME", 
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = neonPink)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgDark,
                    titleContentColor = neonPink
                )
            )
        },
        containerColor = bgDark
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

            // 1. RESULT TERMINAL (Tech Card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(topStart = 0.dp, bottomEnd = 24.dp))
                    .background(panelBg)
                    .border(1.dp, neonPink.copy(alpha = 0.5f), CutCornerShape(topStart = 0.dp, bottomEnd = 24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "> TARGET_DATE_LOCKED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = neonPink
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    if (eraObj != null) {
                         // Tech Divider
                        Box(Modifier.fillMaxWidth().height(1.dp).background(neonPink.copy(alpha = 0.3f)))
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            "> JAPAN_ERA_DETECTED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = eraObj.kanji,
                                fontFamily = FontFamily.Default, // Kanji needs normal font
                                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                                color = neonPink,
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$eraYearDisplay",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Text(
                            text = "(${eraObj.english.uppercase()} PERIOD)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = neonPink.copy(alpha = 0.7f),
                            letterSpacing = 2.sp
                        )
                    } else {
                         Spacer(modifier = Modifier.height(32.dp))
                         Text("[ERROR: DATE_OUT_OF_RANGE]", fontFamily = FontFamily.Monospace, color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. INPUT BUTTON
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .springyTouch(),
                shape = CutCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, neonPink)
            ) {
                Icon(Icons.Default.EditCalendar, null, tint = neonPink)
                Spacer(modifier = Modifier.width(8.dp))
                Text("MODIFY_TEMPORAL_COORDINATES", fontFamily = FontFamily.Monospace, color = neonPink)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 3. QUICK JUMPS (Terminal Commands)
            Text(
                "> EXECUTE_QUICK_JUMP", 
                fontFamily = FontFamily.Monospace, 
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val quickDates = listOf(
                "NOW" to LocalDate.now(),
                "REIWA_01" to LocalDate.of(2019, 5, 1),
                "HEISEI_01" to LocalDate.of(1989, 1, 8),
                "SHOWA_01" to LocalDate.of(1926, 12, 25)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                quickDates.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { (label, date) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clickable { selectedDate = date }
                                    .background(panelBg)
                                    .border(1.dp, Color.Gray.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label, 
                                    fontFamily = FontFamily.Monospace, 
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
