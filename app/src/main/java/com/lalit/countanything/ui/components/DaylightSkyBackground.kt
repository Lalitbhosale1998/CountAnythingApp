package com.lalit.countanything.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DaylightSkyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SkyTransition")

    // --- BREATHE ANIMATION ---
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SkyBreathe"
    )

    // --- DRIFT ANIMATION ---
    val driftOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudDrift"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f

            // 1. Base Sky Gradient (Soft Blue to White)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD), // Light Blue 50
                        Color(0xFFF3E5F5)  // Lavender 50
                    )
                )
            )

            // 2. Soft Sun Glow (Top Left)
            drawRadialGradient(
                colors = listOf(Color(0xFFFFF3E0).copy(alpha = 0.8f), Color.Transparent),
                center = Offset(0f, 0f),
                radius = canvasWidth * 0.8f
            )

            // 3. Floating "Clouds" (Soft White/Pink blobs)
            // Cloud 1
            drawRadialGradient(
                colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent),
                center = Offset(centerX + driftOffset, centerY - 200f),
                radius = canvasWidth * 0.6f * breatheScale
            )

            // Cloud 2 (Pinkish)
            drawRadialGradient(
                colors = listOf(Color(0xFFFCE4EC).copy(alpha = 0.4f), Color.Transparent), // Pink 50
                center = Offset(centerX - driftOffset - 100f, centerY + 300f),
                radius = canvasWidth * 0.7f
            )

            // 4. Subtle Aurora (Teal)
            drawRadialGradient(
                colors = listOf(Color(0xFFE0F2F1).copy(alpha = 0.5f), Color.Transparent), // Teal 50
                center = Offset(canvasWidth, centerY),
                radius = canvasWidth * 0.5f * breatheScale
            )
        }
        content()
    }
}

private fun DrawScope.drawRadialGradient(
    colors: List<Color>,
    center: Offset,
    radius: Float
) {
    drawRect(
        brush = Brush.radialGradient(
            colors = colors,
            center = center,
            radius = radius
        )
    )
}
