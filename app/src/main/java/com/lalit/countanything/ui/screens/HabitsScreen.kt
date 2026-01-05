package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.R
import com.lalit.countanything.ui.components.*
import com.lalit.countanything.ui.models.Counter
import com.lalit.countanything.ui.models.CounterType

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    counterTitle: String,
    cigaretteCount: Float,
    onAddOne: () -> Unit,
    onSubtractOne: () -> Unit,
    onReset: () -> Unit,
    currencySymbol: String,
    genericCounters: List<Counter> = emptyList(),
    onUpdateCounter: (String, Float) -> Unit = { _, _ -> },
    onDeleteCounter: (String) -> Unit = {},
    fixedSexualHealthCounter: Counter,
    onIncrementSexualHealth: () -> Unit,
    onDecrementSexualHealth: () -> Unit,
    selectedDate: java.time.LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetToToday: () -> Unit,
    isDarkTheme: Boolean = isSystemInDarkTheme() // Added parameter with default
) {
    // Separate counters by type
    val standardCounters = genericCounters.filter { it.type == CounterType.STANDARD }
    val sexualHealthCounters = genericCounters.filter { it.type == CounterType.SEXUAL_HEALTH }
    
    // Select background and text colors based on theme
    val ScreenContent = @Composable {
        Scaffold(
            containerColor = Color.Transparent, 
            contentColor = if(isDarkTheme) Color.White else Color.Black 
        ) { padding ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(
                    top = 16.dp + padding.calculateTopPadding(),
                    start = 16.dp, 
                    end = 16.dp, 
                    bottom = 120.dp + padding.calculateBottomPadding()
                ),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp
            ) {
                // --- HEADER SECTION (Full Width) ---
                item(span = StaggeredGridItemSpan.FullLine) {
                     Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Your Habits",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = if(isDarkTheme) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                         Text(
                            text = "Tracking your daily progress",
                            style = MaterialTheme.typography.bodyLarge,
                            // Dynamic secondary color
                            color = if(isDarkTheme) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                     }
                }
    
                // --- HERO MAIN COUNTER (Full Width) ---
                item(span = StaggeredGridItemSpan.FullLine) {
                    HeroLiquidCard(
                        title = counterTitle,
                        count = cigaretteCount,
                        onAdd = onAddOne,
                        onSubtract = onSubtractOne,
                        onReset = onReset
                    )
                }
    
                // --- SEXUAL HEALTH TRACKER (Full Width for now) ---
                item(span = StaggeredGridItemSpan.FullLine) {
                     SexualHealthTrackerCard(
                        counter = fixedSexualHealthCounter,
                        onIncrement = onIncrementSexualHealth,
                        onDecrement = onDecrementSexualHealth,
                        onDelete = null, 
                        selectedDate = selectedDate
                    )
                }
                
                 // --- DYNAMIC SEXUAL HEALTH TRACKERS ---
                 items(sexualHealthCounters, key = { it.id }) { counter ->
                     SexualHealthTrackerCard(
                        counter = counter,
                        onIncrement = { onUpdateCounter(counter.id, 1f) },
                        onDecrement = { onUpdateCounter(counter.id, -1f) },
                        onDelete = { onDeleteCounter(counter.id) },
                        selectedDate = selectedDate
                    )
                }
    
                // --- SECTION TITLE ---
                if (standardCounters.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = "Quick Trackers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = if(isDarkTheme) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
    
                // --- STANDARD COUNTERS (2 Columns) ---
                items(standardCounters, key = { it.id }) { counter ->
                     BubbleCounterCard(
                        counter = counter,
                        onIncrement = { onUpdateCounter(counter.id, 1f) },
                        onDecrement = { onUpdateCounter(counter.id, -1f) },
                        onDelete = { onDeleteCounter(counter.id) },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (isDarkTheme) {
        GalacticNebulaBackground(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenContent()
        }
    } else {
        DaylightSkyBackground(
             modifier = Modifier.fillMaxSize()
        ) {
            ScreenContent()
        }
    }
}

@Composable
fun HeroLiquidCard(
    title: String,
    count: Float,
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(48.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Liquid Background Effect
            val progress = (count / 20f).coerceIn(0f, 1f)
            
             LiquidProgress(
                 modifier = Modifier
                     .fillMaxWidth()
                     .align(Alignment.BottomCenter)
                     .fillMaxHeight(0.3f + (progress * 0.7f)),
                 color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
             )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                // Large Animated Number
                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                    },
                    label = "HeroCount"
                ) { targetCount ->
                     Text(
                        text = "%.0f".format(targetCount),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.onSurface 
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     SqueezeButton(
                        onClick = onSubtract,
                        icon = Icons.Default.Remove,
                        color = MaterialTheme.colorScheme.background,
                        iconColor = MaterialTheme.colorScheme.onBackground
                    )

                    // Big Add Button
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { onAdd() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                     SqueezeButton(
                        onClick = onReset,
                        icon = Icons.Default.Refresh,
                        color = MaterialTheme.colorScheme.background,
                        iconColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
