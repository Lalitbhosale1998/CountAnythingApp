package com.lalit.countanything.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiquidProgress(
    modifier: Modifier = Modifier,
    color: Color = Color.Cyan,
    animating: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidWiggle")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LiquidPhase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        
        val path = Path()
        path.moveTo(0f, centerY)

        // Draw a sine wave
        for (x in 0..width.toInt()) {
            val normalizedX = x / width
            val waveHeight = 20f // Amplitude
            val frequency = 2f * Math.PI.toFloat() // Waves across width
            
            // Complex wave: sum of two sines for more "liquid" feel
            val y = centerY + 
                    (sin(normalizedX * frequency + phase) * waveHeight) +
                    (cos(normalizedX * frequency * 1.5f + phase) * (waveHeight / 2))

            path.lineTo(x.toFloat(), y)
        }

        // Close shape to bottom
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()

        drawPath(
            path = path,
            color = color.copy(alpha = 0.6f),
            style = Fill
        )
        
        // Second wave (offset)
        val path2 = Path()
        path2.moveTo(0f, centerY)
        for (x in 0..width.toInt()) {
            val normalizedX = x / width
            val waveHeight = 15f
            val frequency = 2f * Math.PI.toFloat()
            
            val y = centerY + 50f +
                    (sin(normalizedX * frequency + phase + 1.5f) * waveHeight)

            path2.lineTo(x.toFloat(), y)
        }
        path2.lineTo(width, height)
        path2.lineTo(0f, height)
        path2.close()
        
         drawPath(
            path = path2,
            color = color,
            style = Fill
        )
    }
}
