package com.lalit.countanything.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lalit.countanything.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun FinanceHeader(
    totalSavings: Float,
    currencySymbol: String,
    privacyModeEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 42.dp, bottomEnd = 42.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFAB40), // Vibrant Amber
                        Color(0xFF9C27B0)  // Deep Purple
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Monthly Savings",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 1f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            SensitiveText(
                text = "$currencySymbol${"%,.0f".format(totalSavings)}",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                ),
                privacyModeEnabled = privacyModeEnabled
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mini Sparkline placeholder
            FinancialSparkline(
                data = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.8f, 1f), // Dummy trend
                modifier = Modifier
                    .width(120.dp)
                    .height(30.dp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun FinancialSparkline(
    data: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        
        val path = androidx.compose.ui.graphics.Path().apply {
            data.forEachIndexed { index, value ->
                val x = index * stepX
                val y = height - (value * height)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownModule(
    title: String,
    targetDate: LocalDate?,
    onEditDate: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val daysRemaining = remember(targetDate) {
        targetDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
    }

    Card(
        modifier = Modifier.fillMaxWidth().springyTouch(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            AnimatedContent(targetState = daysRemaining, label = "CountdownAnim") { days ->
                Text(
                    text = days?.toString() ?: "??",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        brush = Brush.verticalGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
                )
            }
            Text(
                "Days Left",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            val progress = remember(targetDate, daysRemaining) {
                if (targetDate == null || daysRemaining == null) 0f
                else {
                    val totalDays = 30f
                    ((totalDays - daysRemaining.coerceAtMost(30)) / totalDays).coerceIn(0f, 1f)
                }
            }
            WavyProgressBar(progress = progress, modifier = Modifier.padding(horizontal = 8.dp))

            Spacer(modifier = Modifier.height(16.dp))
            PulseButton(
                onClick = onEditDate,
                icon = Icons.Default.EditCalendar,
                contentDescription = "Edit",
                pulseColor = Color(0xFF2196F3),
                modifier = Modifier.size(60.dp),
                iconSize = 24
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BudgetHubModule(
    title: String,
    displayedMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    salary: Float,
    savings: Float,
    currencySymbol: String,
    privacyModeEnabled: Boolean,
    onEdit: () -> Unit,
    onResetMonth: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val spent = (salary - savings).coerceAtLeast(0f)
    val progress = if (salary > 0f) (savings / salary).coerceIn(0f, 1f) else 0f
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val isCurrentMonth = displayedMonth == YearMonth.now()

    Card(
        modifier = Modifier.fillMaxWidth().springyTouch(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onMonthChange(displayedMonth.minusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = displayedMonth.format(monthFormatter),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { onMonthChange(displayedMonth.plusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, modifier = Modifier.size(24.dp))
                }
            }
            
            if (!isCurrentMonth) {
                Surface(
                    onClick = onResetMonth,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Go to Today",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 8.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Saved",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    FinancialMetricRow(
                        label = "Salary",
                        value = "$currencySymbol${"%,.0f".format(salary)}",
                        color = MaterialTheme.colorScheme.primary,
                        privacyModeEnabled = privacyModeEnabled
                    )
                    Spacer(Modifier.height(12.dp))
                    FinancialMetricRow(
                        label = "Savings",
                        value = "$currencySymbol${"%,.0f".format(savings)}",
                        color = Color(0xFF4CAF50),
                        privacyModeEnabled = privacyModeEnabled
                    )
                    Spacer(Modifier.height(12.dp))
                    FinancialMetricRow(
                        label = "Spent",
                        value = "$currencySymbol${"%,.0f".format(spent)}",
                        color = MaterialTheme.colorScheme.error,
                        privacyModeEnabled = privacyModeEnabled
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulseButton(
                    onClick = onEdit,
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit",
                    pulseColor = Color(0xFF2196F3),
                    modifier = Modifier.size(60.dp)
                )
                
                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(32.dp))
                    PulseButton(
                        onClick = onDelete,
                        icon = Icons.Default.Delete,
                        contentDescription = "Delete",
                        pulseColor = Color(0xFFFF5252),
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialMetricRow(
    label: String,
    value: String,
    color: Color,
    privacyModeEnabled: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SensitiveText(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                privacyModeEnabled = privacyModeEnabled
            )
        }
    }
}

@Composable
fun CumulativeTotalModule(
    title: String,
    total: Float,
    currencySymbol: String,
    privacyModeEnabled: Boolean,
    onAddAmount: () -> Unit,
    onEditTotal: () -> Unit,
    onDelete: (() -> Unit)? = null,
    pulseTrigger: Long = 0L
) {
    val pulseProgress = remember { Animatable(-1.2f) }
    
    LaunchedEffect(pulseTrigger) {
        if (pulseTrigger > 0) {
            pulseProgress.snapTo(-1.2f)
            pulseProgress.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            )
            pulseProgress.snapTo(-1.2f)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().springyTouch().clickable { onAddAmount() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (pulseProgress.value > -1.2f && pulseProgress.value < 1.2f) {
                val flagColors = listOf(
                    Color.Transparent,
                    Color(0xFFFF9933).copy(alpha = 0.4f),
                    Color.White.copy(alpha = 0.6f),
                    Color(0xFF138808).copy(alpha = 0.4f),
                    Color.Transparent
                )
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val xPos = pulseProgress.value * width
                    val gradientWidth = width * 0.7f 
                    
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = flagColors,
                            startX = xPos - gradientWidth,
                            endX = xPos + gradientWidth
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp).padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    SensitiveText(
                        text = "$currencySymbol${"%,.0f".format(total)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                        ),
                        privacyModeEnabled = privacyModeEnabled
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulseButton(
                        onClick = onEditTotal,
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit",
                        pulseColor = Color(0xFF2196F3),
                        modifier = Modifier.size(56.dp)
                    )
                    
                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        PulseButton(
                            onClick = onDelete,
                            icon = Icons.Default.Delete,
                            contentDescription = "Delete",
                            pulseColor = Color(0xFFFF5252),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    PulseButton(
                        onClick = onAddAmount,
                        icon = Icons.Default.AddCircle,
                        contentDescription = "Add",
                        pulseColor = Color(0xFF00E676),
                        modifier = Modifier.size(64.dp),
                        iconSize = 32
                    )
                }
            }
        }
    }
}
