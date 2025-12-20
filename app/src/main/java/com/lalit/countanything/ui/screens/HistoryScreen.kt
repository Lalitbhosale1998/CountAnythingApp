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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalit.countanything.ui.components.AnimatedColumn
import com.lalit.countanything.ui.components.CigaretteHistoryCalendar
import com.lalit.countanything.ui.components.AnimatedItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HistoryScreen(
    history: Map<String, Int>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit) {
    AnimatedColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedItem(index = 0) {
            // --- THE OLD CALENDAR (STAYING) ---
            CigaretteHistoryCalendar(
                history = history,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
        }

        // --- BOTTOM SPACER FOR BREATHING ROOM ---
        Spacer(modifier = Modifier.height(80.dp))
    }
}
