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
import java.time.LocalDate

@Composable
fun HistoryChart(
    history: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val daysToShow = 14
    val data = (0 until daysToShow).map { i ->
        val date = today.minusDays(i.toLong())
        date to (history[date.toString()] ?: 0)
    }.reversed()

    val maxCount = data.maxByOrNull { it.second }?.second ?: 1

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Text(
            text = "Daily Trends (Last 14 Days)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (date, count) ->
                val barHeight = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(barHeight.coerceAtLeast(0.05f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (date == today) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    // Date Label
                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 10.sp,
                        color = if (date == today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (date == today) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
