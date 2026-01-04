package com.lalit.countanything.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.lalit.countanything.R
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.utils.SexualHealthConstants
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SexualHealthTrackerCard(
    modifier: Modifier = Modifier,
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

    val feedbackResId = remember(weeklyCount) {
        SexualHealthConstants.getFeedback(weeklyCount)
    }
    val feedback = stringResource(feedbackResId)

    // --- HEARTBEAT ANIMATION ---
    val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatTransition")
    val heartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f, 
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartbeatScale"
    )

    // Wrap in Box to allow scaling without clipping
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .scale(heartbeatScale)
                .springyTouch(),
            shape = RoundedCornerShape(48.dp), // More playful roundness
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient( // Diagonal gradient for more dynamism
                            colors = listOf(
                                Color(0xFFFF512F), // Fanta Orange
                                Color(0xFFDD2476), // Bloody Mary
                                Color(0xFF9b59b6)  // Amethyst
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // --- HEADER ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = counter.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- BIG STATS ROW ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Today (Huge)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TODAY",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.0f".format(counter.count),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 64.sp // Massive impact
                                ),
                                color = Color.White,
                                lineHeight = 64.sp
                            )
                        }
                        
                        // Weekly (Subtle sidekick)
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "THIS WEEK",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = weeklyCount.toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- GLASS TIP BOX ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.15f)) // Glassy
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = feedback,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White.copy(alpha = 0.95f),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CONTROL DECK ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         // Small Remove Button
                        SqueezeButton(
                            onClick = onDecrement,
                            icon = Icons.Default.Remove,
                            color = Color.White.copy(alpha = 0.2f),
                            iconColor = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Big Add Button (Primary Action)
                        SqueezeButton(
                            onClick = onIncrement,
                            icon = Icons.Default.Add,
                            color = Color.White,
                            iconColor = Color(0xFFDD2476), // Matches gradient
                            modifier = Modifier.size(80.dp), // Hero button
                        )
                    }
                }
            }
        }
    }
}
