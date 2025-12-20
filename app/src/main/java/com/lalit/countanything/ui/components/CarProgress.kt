package com.lalit.countanything.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun CarProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    // Animate the progress for smoothness
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(modifier = modifier.aspectRatio(2.5f)) { // Maintain aspect ratio for the car
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Scale the path to fit the canvas
            val pathScaleX = width / 100f 
            val pathScaleY = height / 40f // Coordinate system grounded on 40 units height
            
            // Define the Toyota GR86-ish Silhouette Path
            val carPath = Path().apply {
                // Start at front bottom bumper
                moveTo(5f * pathScaleX, 35f * pathScaleY)
                // Front Bumper curve up
                cubicTo(
                    5f * pathScaleX, 30f * pathScaleY, 
                    7f * pathScaleX, 28f * pathScaleY, 
                    10f * pathScaleX, 25f * pathScaleY
                )
                // Headlight / Hood
                lineTo(25f * pathScaleX, 22f * pathScaleY)
                // Windshield
                lineTo(40f * pathScaleX, 10f * pathScaleY)
                // Roof
                lineTo(65f * pathScaleX, 10f * pathScaleY)
                // Rear Window / Trunk declination
                lineTo(85f * pathScaleX, 22f * pathScaleY)
                // Trunk / Spoiler lip
                lineTo(95f * pathScaleX, 20f * pathScaleY)
                // Rear Bumper down
                lineTo(95f * pathScaleX, 35f * pathScaleY)
                // Bottom chassis connect
                lineTo(5f * pathScaleX, 35f * pathScaleY)
                close()
            }
            
            // Draw Wheels (as separate circles for better detail)
            val wheelRadius = 5.5f * pathScaleX
            val frontWheelCenter = Offset(20f * pathScaleX, 35f * pathScaleY)
            val rearWheelCenter = Offset(80f * pathScaleX, 35f * pathScaleY)

            // Function to draw the complete car (body + wheels)
            fun DrawScope.drawCar(color: Color, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
                drawPath(carPath, color = color, style = style)
                drawCircle(color, radius = wheelRadius, center = frontWheelCenter, style = style)
                drawCircle(color, radius = wheelRadius, center = rearWheelCenter, style = style)
            }

            // 1. Draw the Track (Empty Outline)
            drawCar(color = trackColor, style = Stroke(width = 4.dp.toPx()))

            // 2. Draw the Fill (Clipped based on progress)
            clipRect(
                left = 0f,
                top = 0f,
                right = width * animatedProgress.value,
                bottom = height
            ) {
                drawCar(color = fillColor, style = androidx.compose.ui.graphics.drawscope.Fill)
            }
        }
    }
}
