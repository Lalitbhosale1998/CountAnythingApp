package com.lalit.countanything.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import kotlin.math.PI
import kotlin.math.sin

@SuppressLint("RestrictedApi")
@Composable
fun WavyProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    // This state will be used to animate the wave's phase, making it move
    val wavePhase by rememberInfiniteTransition(label = "WavePhase").animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "WaveAnimation"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp) // Taller for "Bold" presence
    ) {
        val strokeWidth = 16.dp.toPx() // Significantly thicker stroke
        val progressEndPx = size.width * progress

        // 1. Draw the wavy filled portion
        if (progress > 0f) {
            val wavePath = Path()
            val waveAmplitude = 10.dp.toPx() // Bolder, more pronounced waves
            val waveFrequency = 0.04f 

            wavePath.moveTo(0f, center.y)
            for (x in 0..progressEndPx.toInt()) {
                val y = center.y + sin(x * waveFrequency + wavePhase) * waveAmplitude
                wavePath.lineTo(x.toFloat(), y)
            }
            
            drawPath(
                path = wavePath,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 2. Draw the remaining track
        if (progress < 1f) {
            drawLine(
                color = trackColor,
                start = Offset(x = progressEndPx, y = center.y),
                end = Offset(x = size.width, y = center.y),
                strokeWidth = strokeWidth, // Match the wave thickness
                cap = StrokeCap.Round
            )
        }
    }
}
