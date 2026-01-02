package com.lalit.countanything.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalit.countanything.R
import com.lalit.countanything.ui.components.AnimatedColumn
import com.lalit.countanything.ui.components.HabitHistoryCalendar
import com.lalit.countanything.ui.components.AnimatedItem
import com.lalit.countanything.ui.models.Counter
import java.time.LocalDate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.statusBarsPadding
import com.lalit.countanything.ui.models.CounterType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HistoryScreen(
    history: Map<String, Float>,
    sexualHealthHistory: Map<String, Float>,
    genericCounters: List<Counter>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // --- AURA GRADIENTS ---
    val healthAura = Brush.verticalGradient(
        colors = listOf(Color(0xFFD32F2F), Color(0xFF7B1FA2)) // Ruby to Violet
    )
    val cigaretteAura = Brush.verticalGradient(
        colors = listOf(Color(0xFF424242), Color(0xFFFFA000)) // Smoky Gray to Deep Orange
    )
    val standardAura = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D47A1), Color(0xFF00B0FF)) // Deep Blue to Neon Blue
    )

    AnimatedColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- PRIMARY HABIT: CIGARETTE COUNTER ---
        AnimatedItem(index = 0) {
            androidx.compose.runtime.key("cigarette_calendar_std") {
                HabitHistoryCalendar(
                    title = stringResource(R.string.cigarette_history_log),
                    history = history,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    aura = cigaretteAura
                )
            }
        }

        // --- PERMANENT HABIT: SEXUAL HEALTH & VITALITY ---
        AnimatedItem(index = 1) {
            HabitHistoryCalendar(
                title = "Sexual Health & Vitality Log",
                history = sexualHealthHistory,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                aura = healthAura
            )
        }

        // --- SECONDARY HABITS: CUSTOM TRACKERS ---
        genericCounters.forEachIndexed { index, counter ->
            val counterAura = if (counter.type == CounterType.SEXUAL_HEALTH) healthAura else standardAura
            
            AnimatedItem(index = index + 2) {
                HabitHistoryCalendar(
                    title = "${counter.title} History Log",
                    history = counter.history,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    aura = counterAura
                )
            }
        }

        // --- BOTTOM SPACER FOR BREATHING ROOM ---
        Spacer(modifier = Modifier.height(80.dp))
    }
}
