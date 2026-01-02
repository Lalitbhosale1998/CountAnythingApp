package com.lalit.countanything.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.utils.SexualHealthConstants
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SexualHealthTrackerCard(
    counter: Counter,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: (() -> Unit)? = null,
    selectedDate: LocalDate = LocalDate.now()
) {
    // --- WEEKLY FREQUENCY LOGIC ---
    val weeklyCount = remember(counter.history, selectedDate) {
        val startOfWeek = selectedDate.with(java.time.DayOfWeek.MONDAY)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        
        counter.history.entries.filter { (dateStr, _) ->
            try {
                val date = LocalDate.parse(dateStr, formatter)
                (date.isAfter(startOfWeek) || date.isEqual(startOfWeek)) && 
                (date.isBefore(selectedDate) || date.isEqual(selectedDate))
            } catch (e: Exception) {
                false
            }
        }.sumOf { it.value.toDouble() }.toInt()
    }

    val feedback = remember(weeklyCount) {
        SexualHealthConstants.getFeedback(weeklyCount)
    }

    // --- HEARTBEAT ANIMATION ---
    val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatTransition")
    val heartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartbeatScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(heartbeatScale)
            .springyTouch(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFD32F2F), // Deep Ruby
                            Color(0xFF7B1FA2)  // Vibrant Violet
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = counter.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Spacer to maintain layout balance if needed, or just nothing.
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- DYNAMIC MEDICAL FEEDBACK ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = feedback,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "%.0f".format(counter.count),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Weekly",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = weeklyCount.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulseButton(
                            onClick = onDecrement,
                            icon = Icons.Default.Remove,
                            contentDescription = "Minus",
                            pulseColor = Color(0xFFF44336), // Light Ruby
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        PulseButton(
                            onClick = onIncrement,
                            icon = Icons.Default.Add,
                            contentDescription = "Add",
                            pulseColor = Color(0xFFE91E63), // Pinkish Ruby
                            modifier = Modifier.size(80.dp),
                            iconSize = 32
                        )
                    }
                }
            }
        }
    }
}
