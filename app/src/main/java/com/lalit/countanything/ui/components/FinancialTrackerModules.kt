package com.lalit.countanything.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun FinanceHeader(
    totalSavings: Float,
    currencySymbol: String,
    privacyModeEnabled: Boolean,
    displayedMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD700), // Gold
                        Color(0xFFFF8C00), // Dark Orange
                        Color(0xFFFF0080)  // Fuschia
                    )
                )
            )
    ) {
        // Shine effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = size.width,
                center = androidx.compose.ui.geometry.Offset(size.width, 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: Month Nav + Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Month Navigation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onMonthChange(displayedMonth.minusMonths(1)) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.finance_total_monthly_savings).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.5.sp, 
                                fontSize = 8.sp, 
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = displayedMonth.format(DateTimeFormatter.ofPattern("MMM yyyy")).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { onMonthChange(displayedMonth.plusMonths(1)) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                SqueezeButton(
                    onClick = onAdd,
                    icon = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add),
                    color = Color.White.copy(alpha = 0.2f),
                    iconColor = Color.White,
                    modifier = Modifier.size(40.dp),
                    iconSize = 20.dp
                )
            }
            
            // Amount Display (Centered/Prominent now that Top is busy?) 
            // Actually keeping it left aligned below is fine, but lets give it space
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SensitiveText(
                    text = "$currencySymbol${"%,.0f".format(totalSavings)}",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    ),
                    privacyModeEnabled = privacyModeEnabled
                )
                
                 // Sparkline Container (Smaller now to fit layout)
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    FinancialSparkline(
                        data = listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.7f, 1f),
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    )
                }
            }
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
    workingDays: Long? = null,
    commuteCost: Float = 0f,
    currencySymbol: String = "¥",
    onEditDate: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val daysRemaining = remember(targetDate) {
        targetDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
    }

    Card(
        modifier = Modifier.fillMaxWidth().springyTouch(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2C3E50),
                            Color(0xFF4CA1AF)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = title, 
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete), tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Number
                // Hero Number with Circular Progress
                Box(contentAlignment = Alignment.Center) {
                    val progress = remember(targetDate, daysRemaining) {
                        if (targetDate == null || daysRemaining == null) 0f
                        else {
                            val totalDays = 30f
                            ((totalDays - daysRemaining.coerceAtMost(30)) / totalDays).coerceIn(0f, 1f)
                        }
                    }
                    
                    // Track
                    WavyCircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.size(260.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        trackColor = Color.Transparent,
                        strokeWidth = 35.dp.value, 
                        amplitude = 4.dp.value
                    )
                    
                    // Progress
                    WavyCircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(260.dp),
                        color = Color(0xFF00E676), // Neon Green
                        trackColor = Color.Transparent,
                        strokeWidth = 35.dp.value,
                        amplitude = 4.dp.value
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Determine what to show as the primary large number
                        val primaryValue = if (workingDays != null) workingDays else daysRemaining
                        val secondaryValue = if (workingDays != null) daysRemaining else null
                        
                        val primaryLabel = if (workingDays != null) "WORKING DAYS" else stringResource(R.string.finance_days_left_label).uppercase()
                        
                        AnimatedContent(targetState = primaryValue, label = "CountdownAnim") { number ->
                            Text(
                                text = number?.toString() ?: "??",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 64.sp,
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = Color.Black.copy(alpha = 0.3f),
                                        blurRadius = 12f
                                    )
                                ),
                                color = Color.White
                            )
                        }
                        Text(
                            primaryLabel,
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        
                        if (secondaryValue != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "$secondaryValue TOTAL DAYS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }


                    }
                }

                Spacer(modifier = Modifier.height(32.dp))


                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SqueezeButton(
                        onClick = onEditDate,
                        icon = Icons.Default.EditCalendar,
                        contentDescription = stringResource(R.string.cd_edit),
                        color = Color.White.copy(alpha = 0.2f),
                        iconColor = Color.White,
                        modifier = Modifier.size(56.dp),
                        iconSize = 24.dp
                    )

                    if (commuteCost > 0) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFF9800))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "EST. COMMUTE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                                    color = Color(0xFFFF9800) // Solid Orange
                                )
                                Text(
                                    "$currencySymbol${commuteCost.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White // White for readability
                                )
                            }
                        }
                    }
                }
            }
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
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF134E5E), // Deep Teal
                            Color(0xFF71B280)  // Minty Green
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Month Nav
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onMonthChange(displayedMonth.minusMonths(1)) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                         Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = displayedMonth.format(monthFormatter),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    IconButton(onClick = { onMonthChange(displayedMonth.plusMonths(1)) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
                
                if (!isCurrentMonth) {
                    Surface(
                        onClick = onResetMonth,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.finance_go_to_today),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Gauge & Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gauge
                    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 12.dp,
                            trackColor = Color.Black.copy(alpha = 0.2f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 12.dp,
                            color = Color(0xFFC0FF00), // Lime Neon
                            trackColor = Color.Transparent,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                "SAVED",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Metrics
                    Column(modifier = Modifier.weight(1f)) {
                        FinancialMetricRow(
                            label = stringResource(R.string.finance_salary_label),
                            value = "$currencySymbol${"%,.0f".format(salary)}",
                            color = Color.White,
                            privacyModeEnabled = privacyModeEnabled
                        )
                        Spacer(Modifier.height(16.dp))
                        FinancialMetricRow(
                            label = stringResource(R.string.finance_savings_label),
                            value = "$currencySymbol${"%,.0f".format(savings)}",
                            color = Color(0xFFC0FF00),
                            privacyModeEnabled = privacyModeEnabled
                        )
                        Spacer(Modifier.height(16.dp))
                        FinancialMetricRow(
                            label = stringResource(R.string.finance_spent_label),
                            value = "$currencySymbol${"%,.0f".format(spent)}",
                            color = Color(0xFFFF5252),
                            privacyModeEnabled = privacyModeEnabled
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SqueezeButton(
                        onClick = onEdit,
                        icon = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit),
                        color = Color.White.copy(alpha = 0.2f),
                        iconColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                    
                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(32.dp))
                        SqueezeButton(
                            onClick = onDelete,
                            icon = Icons.Default.Delete,
                            contentDescription = "Delete",
                            color = Color(0xFFFF5252).copy(alpha = 0.2f),
                            iconColor = Color(0xFFFF5252),
                            modifier = Modifier.size(56.dp)
                        )
                    }
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
    Column {
        Text(
            label.uppercase(), 
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), 
            color = Color.White.copy(alpha = 0.6f)
        )
        SensitiveText(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = color,
            privacyModeEnabled = privacyModeEnabled
        )
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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF654ea3), // Ultra Voilet
                        Color(0xFFeaafc8)  // Premium Pink
                    )
                )
            )
        ) {
            // Pulse Effect
            if (pulseProgress.value > -1.2f && pulseProgress.value < 1.2f) {
                 val flagColors = listOf(
                    Color.Transparent,
                    Color(0xFFFF9933).copy(alpha = 0.6f),
                    Color.White.copy(alpha = 0.8f),
                    Color(0xFF138808).copy(alpha = 0.6f),
                    Color.Transparent
                )
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val xPos = pulseProgress.value * width
                    val gradientWidth = width * 0.8f 
                    
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
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = title.uppercase(), 
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp), 
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    SensitiveText(
                        text = "$currencySymbol${"%,.0f".format(total)}",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = Color.White,
                        privacyModeEnabled = privacyModeEnabled
                    )
                }
                
                // Actions
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                     SqueezeButton(
                        onClick = onAddAmount,
                        icon = Icons.Default.AddCircle,
                        contentDescription = stringResource(R.string.cd_add),
                        color = Color.White,
                        iconColor = Color(0xFF654ea3),
                        modifier = Modifier.size(56.dp),
                        iconSize = 28.dp
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                         SqueezeButton(
                            onClick = onEditTotal,
                            icon = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.cd_edit),
                            color = Color.White.copy(alpha = 0.2f),
                            iconColor = Color.White,
                            modifier = Modifier.size(40.dp),
                            iconSize = 18.dp
                        )
                        if (onDelete != null) {
                            SqueezeButton(
                                onClick = onDelete,
                                icon = Icons.Default.Delete,
                                contentDescription = "Delete",
                                color = Color(0xFFFF5252).copy(alpha = 0.2f),
                                iconColor = Color(0xFFFF5252),
                                modifier = Modifier.size(40.dp),
                                iconSize = 18.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
