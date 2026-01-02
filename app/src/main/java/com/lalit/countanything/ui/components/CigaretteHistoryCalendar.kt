package com.lalit.countanything.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.ui.models.CalendarDay
import com.lalit.countanything.ui.models.EmptyDay
import com.lalit.countanything.ui.models.MonthDay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.lalit.countanything.R
import androidx.compose.ui.res.stringResource

@Composable
fun HabitHistoryCalendar(
    modifier: Modifier = Modifier,
    title: String,
    history: Map<String, Float>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    aura: androidx.compose.ui.graphics.Brush? = null
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val historyDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(selectedDate) {
        if (YearMonth.from(selectedDate) != currentMonth) {
            currentMonth = YearMonth.from(selectedDate)
        }
    }

    val calendarDays = remember(currentMonth) {
        val days = mutableListOf<CalendarDay>()
        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
        val daysInMonth = currentMonth.lengthOfMonth()
        for (i in 0 until firstDayOfMonth) {
            days.add(EmptyDay())
        }
        for (day in 1..daysInMonth) {
            days.add(MonthDay(day))
        }
        days
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (aura != null) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.fillMaxWidth().then(if (aura != null) Modifier.background(aura) else Modifier)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (aura != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
                CalendarHeader(
                    yearMonth = currentMonth,
                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                    isAuraActive = aura != null
                )
            Spacer(modifier = Modifier.height(16.dp))
            DaysOfWeekHeader(isAuraActive = aura != null)
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height((6 * 52).dp)
            ) {
                items(calendarDays) { day ->
                    when (day) {
                        is EmptyDay -> {
                            Box(modifier = Modifier.aspectRatio(1f))
                        }
                        is MonthDay -> {
                            val date = currentMonth.atDay(day.day!!)
                            val historyKey = date.format(historyDateFormatter)
                            val count = history[historyKey]
                            DayCell(
                                day = day.day,
                                count = count,
                                isSelected = date == selectedDate,
                                isAuraActive = aura != null,
                                modifier = Modifier.clickable {
                                    if (!date.isAfter(LocalDate.now())) {
                                        onDateSelected(date)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun CalendarHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    isAuraActive: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft, 
                contentDescription = "Previous Month",
                tint = if (isAuraActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = if (isAuraActive) Color.White else MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                contentDescription = "Next Month",
                tint = if (isAuraActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DaysOfWeekHeader(isAuraActive: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isAuraActive) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    count: Float?,
    isSelected: Boolean,
    isAuraActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val hasCount = count != null && count > 0

    // --- ANIMATION LOGIC ---

    // Animate the corner radius for the squircle effect.
    val cornerRadius by animateFloatAsState(
        targetValue = if (isSelected) 14f else 50f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "SquircleCornerRadius"
    )

    // Animate the background color for selection, counts, or default state.
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && !isAuraActive -> MaterialTheme.colorScheme.primary
            isSelected && isAuraActive -> Color.White.copy(alpha = 0.9f)
            hasCount && isAuraActive -> Color.White.copy(alpha = 0.25f)
            hasCount && !isAuraActive -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            isAuraActive -> Color.White.copy(alpha = 0.1f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "DayCellBackgroundColor"
    )

    // Animate the main text color for better contrast against the changing background.
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected && !isAuraActive -> MaterialTheme.colorScheme.onPrimary
            isSelected && isAuraActive -> Color.Black
            isAuraActive -> Color.White
            hasCount && !isAuraActive -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(300),
        label = "DayCellTextColor"
    )

    // Animate the count text color for better contrast.
    val countColor by animateColorAsState(
        targetValue = when {
            isSelected && !isAuraActive -> MaterialTheme.colorScheme.onPrimary
            isSelected && isAuraActive -> Color.Black.copy(alpha = 0.7f)
            isAuraActive -> Color.White.copy(alpha = 0.9f)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300),
        label = "DayCellCountColor"
    )

    // --- CELL UI ---

    val cellModifier = modifier
        .aspectRatio(1f)
        // Clip the cell using our custom shape with the animated corner radius.
        .clip(SquircleShape(cornerRadius * LocalDensity.current.density))
        // Apply the animated background color.
        .background(backgroundColor)

    Box(
        modifier = cellModifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 15.sp,
                fontWeight = if (hasCount || isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = textColor
            )
            if (hasCount) {
                Text(
                    text = "%.0f".format(count),
                    fontSize = 11.sp,
                    color = countColor,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}


// A custom "squircle" shape that can be animated
class SquircleShape(private val cornerRadius: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rounded(
            RoundRect(
                rect = Rect(0f, 0f, size.width, size.height),
                cornerRadius = CornerRadius(cornerRadius)
            )
        )
    }
}
