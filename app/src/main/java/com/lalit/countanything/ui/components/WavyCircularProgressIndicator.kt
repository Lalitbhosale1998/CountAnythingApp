package com.lalit.countanything.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WavyCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Float = 20f,
    amplitude: Float = 12f,
    frequency: Int = 12
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(2 * PI).toFloat(), // Rotate counter-clockwise visually
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Phase"
    )

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2 - strokeWidth - amplitude
        val center = Offset(size.width / 2, size.height / 2)
        
        // Helper to get point on wavy circle
        fun getWavyPoint(angleRad: Float, currentPhase: Float): Offset {
            val r = radius + amplitude * sin(frequency * angleRad + currentPhase)
            return Offset(
                x = center.x + r * cos(angleRad),
                y = center.y + r * sin(angleRad)
            )
        }

        // 1. Draw Track (Ghost Wave) - varying opacity
        val trackPath = Path()
        val steps = 180
        for (i in 0..steps) {
            val angle = (i.toFloat() / steps) * 2 * PI.toFloat()
            // Make the track wave move slightly slower or different phase for depth? 
            // For now, let's keep it static or matching but dimmer
            val point = getWavyPoint(angle, phase) // Move with same phase for cohesion
            if (i == 0) trackPath.moveTo(point.x, point.y)
            else trackPath.lineTo(point.x, point.y)
        }
        trackPath.close()
        
        drawPath(
            path = trackPath,
            color = trackColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        // 2. Draw Progress (Active Wave)
        if (progress > 0f) {
            val progressPath = Path()
            // Map progress to angle: -90 degrees (top) is start
            val startAngle = -PI.toFloat() / 2
            val sweepAngle = 2 * PI.toFloat() * progress
            val endAngle = startAngle + sweepAngle
            
            val progressSteps = (steps * progress).toInt().coerceAtLeast(2)
            
            for (i in 0..progressSteps) {
                val t = i.toFloat() / progressSteps
                val angle = startAngle + (sweepAngle * t)
                val point = getWavyPoint(angle, phase)
                
                if (i == 0) progressPath.moveTo(point.x, point.y)
                else progressPath.lineTo(point.x, point.y)
            }

            drawPath(
                path = progressPath,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}
