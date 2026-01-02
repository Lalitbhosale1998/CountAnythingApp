package com.lalit.countanything.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    onResetToToday: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- CIGARETTE COUNTER CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = counterTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // RESTORED: Smooth Number Animation
                AnimatedContent(
                    targetState = cigaretteCount,
                    label = "CigaretteCountAnimation",
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut()
                        } else {
                            slideInVertically { height -> -height } + fadeIn() togetherWith
                                    slideOutVertically { height -> height } + fadeOut()
                        }.using(
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
                        text = "%.0f".format(count),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    PulseButton(
                        onClick = onSubtractOne,
                        icon = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.subtract_one),
                        pulseColor = Color(0xFF00E676), // Now Green (Good action!)
                        modifier = Modifier.size(72.dp)
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    PulseButton(
                        onClick = onAddOne,
                        icon = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_one),
                        pulseColor = Color(0xFFFF5252), // Now Red (Bad habit!)
                        modifier = Modifier.size(96.dp),
                        iconSize = 40
                    )

                    Spacer(modifier = Modifier.width(32.dp))

                    PulseButton(
                        onClick = onReset,
                        icon = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.reset),
                        pulseColor = Color(0xFFFFAB40), // More Vibrant Amber
                        modifier = Modifier.size(72.dp)
                    )
                }
            }
        }

        // --- HEALTH & VITALITY SECTION ---
        val standardCounters = genericCounters.filter { it.type == CounterType.STANDARD }
        val sexualHealthCounters = genericCounters.filter { it.type == CounterType.SEXUAL_HEALTH }

        // Permanent Fixed Tracker
        SexualHealthTrackerCard(
            counter = fixedSexualHealthCounter,
            onIncrement = onIncrementSexualHealth,
            onDecrement = onDecrementSexualHealth,
            onDelete = null, // Fixed tracker cannot be deleted
            selectedDate = selectedDate
        )

        // Dynamic Custom Trackers
        sexualHealthCounters.forEach { counter ->
                SexualHealthTrackerCard(
                    counter = counter,
                    onIncrement = { onUpdateCounter(counter.id, 1f) },
                    onDecrement = { onUpdateCounter(counter.id, -1f) },
                    onDelete = { onDeleteCounter(counter.id) },
                    selectedDate = selectedDate
                )
            }

        if (standardCounters.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Trackers",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                textAlign = TextAlign.Start
            )
            standardCounters.forEach { counter ->
                GenericCounterCard(
                    counter = counter,
                    currencySymbol = currencySymbol,
                    onIncrement = { onUpdateCounter(counter.id, 1f) },
                    onDecrement = { onUpdateCounter(counter.id, -1f) },
                    onDelete = { onDeleteCounter(counter.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Floating Tab Bar padding
    }
}
