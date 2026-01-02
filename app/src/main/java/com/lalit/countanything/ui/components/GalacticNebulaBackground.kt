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
fun GalacticNebulaBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NebulaTransition")

    // --- PULSE / BREATHE ---
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScale"
    )

    // --- SWIRL OFFSETS ---
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SwirlAngle"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f

            // 1. Deep Background (Midnight Indigo)
            drawRect(color = Color(0xFF0D1117))

            // 2. Swirling Nebula Core (Violet)
            val violetOffset = Offset(
                x = centerX + sin(angle) * 100f,
                y = centerY + cos(angle) * 100f
            )
            drawRadialGradient(
                colors = listOf(Color(0xFF4A148C).copy(alpha = 0.6f), Color.Transparent),
                center = violetOffset,
                radius = canvasWidth * 0.8f * breatheScale
            )

            // 3. Supernova Flare (Orange)
            val orangeOffset = Offset(
                x = centerX + cos(angle + 2f) * 150f,
                y = centerY + sin(angle + 2f) * 150f
            )
            drawRadialGradient(
                colors = listOf(Color(0xFFFFAB40).copy(alpha = 0.4f), Color.Transparent),
                center = orangeOffset,
                radius = canvasWidth * 0.6f * (2f - breatheScale)
            )

            // 4. Electric Neon Accents (Indigo/Cyan)
            val indigoOffset = Offset(
                x = centerX + sin(angle * 1.5f) * 120f,
                y = centerY + cos(angle * 1.5f) * 120f
            )
            drawRadialGradient(
                colors = listOf(Color(0xFF1A237E).copy(alpha = 0.5f), Color.Transparent),
                center = indigoOffset,
                radius = canvasWidth * 0.7f
            )
            
            // 5. Star Dust (Tiny specs)
            // Note: For performance, we could draw thousands of points, 
            // but for a background, overlapping gradients feel more "Nebula-like".
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
