package com.lalit.countanything.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lalit.countanything.R
import com.lalit.countanything.ui.models.Screen
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavigationBar(
    currentScreen: Screen, 
    onScreenSelected: (Screen) -> Unit,
    onScreenLongClick: (Screen) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val screens = Screen.values()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Push up from system bar
            .padding(bottom = 16.dp, start = 12.dp, end = 12.dp), // Floating Margins
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 16.dp, 
            shape = CircleShape, // Stadium / Capsule Shape
            // Glassy look: Slightly transparent high surface
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
            modifier = Modifier.wrapContentWidth() // Shrink to fit content if possible, but it's scrollable so full width
                .height(72.dp) // Fixed height for consistency
                .widthIn(max = 600.dp) // Prevent being too wide on tablets
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()) // Preserve scrolling
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), 
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEach { screen ->
                    BubbleNavItem(
                        screen = screen,
                        isSelected = currentScreen == screen,
                        onClick = {
                            if (currentScreen != screen) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onScreenSelected(screen)
                            }
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onScreenLongClick(screen)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BubbleNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Style Mapping for "Fluid Bubble" with Neon Vibrancy
    val (bubbleColor, icon) = when (screen) {
        Screen.Habits -> {
            Color(0xFF00E676) to 
                (if (isSelected) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome)
        }
        Screen.Finance -> {
            Color(0xFFFFC107) to 
                (if (isSelected) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet)
        }
        Screen.Goal -> {
            Color(0xFF2196F3) to 
                (if (isSelected) Icons.Filled.Flag else Icons.Outlined.Flag)
        }
        Screen.History -> {
            Color(0xFF9C27B0) to 
                (if (isSelected) Icons.Filled.History else Icons.Outlined.History)
        }
        Screen.Settings -> {
            Color(0xFF607D8B) to 
                (if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings)
        }
        Screen.Study -> {
            Color(0xFF3F51B5) to
                (if (isSelected) Icons.Filled.School else Icons.Outlined.School)
        }
        Screen.Events -> {
            Color(0xFFE91E63) to
                (if (isSelected) Icons.Filled.Event else Icons.Outlined.Event)
        }
        Screen.Tools -> {
             Color(0xFFFF5722) to
                (if (isSelected) Icons.Filled.Build else Icons.Outlined.Build)
        }
    }

    val labelRes = when (screen) {
        Screen.Habits -> R.string.nav_habits
        Screen.Finance -> R.string.nav_finance
        Screen.Goal -> R.string.nav_goal
        Screen.History -> R.string.nav_history
        Screen.Settings -> R.string.nav_settings
        Screen.Study -> R.string.nav_study
        Screen.Events -> R.string.nav_events
        Screen.Tools -> R.string.nav_tools
    }

    val bubbleScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = 0.45f, // Jelly bounce
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "BubbleScale"
    )

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(CircleShape)
            .clip(CircleShape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = {
                     // Haptic feedback for long press
                     // Note: We'll trigger haptic in the lambda if needed
                     onLongClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- THE JELLY BUBBLE ---
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = scaleOut(animationSpec = tween(150)) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
                    .widthIn(min = 110.dp)
                    .graphicsLayer {
                        scaleX = bubbleScale
                        scaleY = bubbleScale
                    }
                    .background(bubbleColor.copy(alpha = 0.2f), CircleShape)
            )
        }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp), // Increased padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(labelRes),
            tint = if (isSelected) bubbleColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp) // Removed translationY
        )
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text = stringResource(labelRes),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = bubbleColor.copy(alpha = 1f)
                    ),
                    maxLines = 1
                )
            }
        }
    }
}


