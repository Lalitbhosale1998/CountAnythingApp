package com.lalit.countanything.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PulseButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    pulseColor: Color,
    modifier: Modifier = Modifier,
    iconSize: Int = 24
) {
    val haptic = LocalHapticFeedback.current
    
    // --- FORCEFUL ANIMATION TRIGGER ---
    var triggerCount by remember { mutableStateOf(0) }
    
    val scaleAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(0f) }
    val jumpAnim = remember { Animatable(0f) }

    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            launch {
                scaleAnim.snapTo(0.5f)
                scaleAnim.animateTo(2.8f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow))
                scaleAnim.animateTo(0f, tween(150))
            }
            launch {
                alphaAnim.snapTo(0.6f)
                delay(120)
                alphaAnim.animateTo(0f, tween(300))
            }
            launch {
                jumpAnim.animateTo(-24f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMediumLow))
                jumpAnim.animateTo(0f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
            }
        }
    }

    // Outer Box: Strictly respects the size passed from outside (e.g. 96.dp)
    // We allow content to overflow so the pulse bubble can be huge
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // --- THE EXPLOSIVE PULSE (Background layer) ---
        Box(
            modifier = Modifier
                .fillMaxSize(0.5f) // Relative to parent size (e.g. 96dp * 0.5)
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    alpha = alphaAnim.value
                }
                .background(pulseColor, CircleShape)
        )

        // --- THE MAIN BUTTON BODY (Strictly fills the modifier size) ---
        Box(
            modifier = Modifier
                .fillMaxSize() 
                .clip(CircleShape)
                .background(pulseColor.copy(alpha = 0.5f)) // DEEP saturated vibrancy
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        triggerCount++
                        onClick()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White.copy(alpha = 0.95f), // High contrast white icon for vibrancy
                modifier = Modifier
                    .size(iconSize.dp)
                    .graphicsLayer { 
                        translationY = jumpAnim.value 
                    }
            )
        }
    }
}
