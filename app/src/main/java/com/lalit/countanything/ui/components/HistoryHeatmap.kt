package com.lalit.countanything.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun HistoryHeatmap(
    history: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val weeksToShow = 20
    val startDate = today.minusWeeks((weeksToShow - 1).toLong()).with(java.time.DayOfWeek.MONDAY)
    
    val maxCount = history.values.maxOrNull() ?: 1

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Text(
            text = "Activity Intensity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Day Labels (M, W, F)
            Column(
                modifier = Modifier.padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Mon", "Wed", "Fri").forEach { day ->
                    Text(day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Grid
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (w in 0 until weeksToShow) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Month label if it's the first week of the month
                        val weekStartDate = startDate.plusWeeks(w.toLong())
                        if (w == 0 || weekStartDate.month != weekStartDate.minusWeeks(1).month) {
                            Text(
                                text = weekStartDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.height(16.dp)
                            )
                        } else {
                            Spacer(Modifier.height(16.dp))
                        }

                        for (d in 0 until 7) {
                            val currentDate = weekStartDate.plusDays(d.toLong())
                            val count = history[currentDate.toString()] ?: 0
                            
                            val color = when {
                                count == 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                count < maxCount / 4 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                count < maxCount / 2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                count < maxCount * 3 / 4 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.primary
                            }

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Less", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf(0.2f, 0.4f, 0.7f, 1.0f).forEach { alpha ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                )
            }
            Text("More", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
