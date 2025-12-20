package com.lalit.countanything.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

/**
 * A modifier that adds a physics-based 3D tilt effect when touched.
 * The element will tilt towards the touch position and spring back on release.
 */
fun Modifier.springyTouch(
    maxRotation: Float = 5f, // Maximum degrees to tilt
    scaleOnPress: Float = 0.98f // Slight scale down for tactile feel
): Modifier = composed {
    val rotationX = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    this
        .graphicsLayer {
            this.rotationX = rotationX.value
            this.rotationY = rotationY.value
            this.scaleX = scale.value
            this.scaleY = scale.value
            this.cameraDistance = 12f * density // Standard camera distance for 3D effect
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                // Wait for the first touch down
                val down = awaitFirstDown()
                
                // Calculate size and center
                val width = size.width.toFloat()
                val height = size.height.toFloat()
                val centerX = width / 2f
                val centerY = height / 2f

                scope.launch {
                    scale.animateTo(
                        targetValue = scaleOnPress,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                }

                // Loop to track drag/move logic if needed, or just calculate on down
                // For a continuous tilt during drag, we would loop until up. 
                // Let's implement continuous tracking for better feel.
                do {
                    val event = currentEvent
                    val position = event.changes.last().position
                    
                    // Calculate "How far from center are we?" (-1 to 1)
                    // X position drives Y axis rotation (tilting left/right)
                    // Y position drives X axis rotation (tilting up/down) - Note the inversion for X
                    
                    val normalizeX = (position.x - centerX) / centerX
                    val normalizeY = (position.y - centerY) / centerY

                    // Tilt Logic:
                    // If I touch TOP (Y < 0 relative to center), it should rotate X POSITIVE (tilt away) or NEGATIVE?
                    // Standard: Top rotates away -> Rotate X positive.
                    // Actually, let's test: Positive X rotation pushes top away.
                    // So if Y is negative (top), Rotation X should be positive.
                    // normalizeY is -1 at top. So we want -normalizeY * max
                    
                    val targetRotationX = -normalizeY * maxRotation * 2f // Multiply to enhance effect
                    val targetRotationY = normalizeX * maxRotation * 2f

                    scope.launch {
                        rotationX.animateTo(
                            targetRotationX, 
                            spring(stiffness = Spring.StiffnessMedium)
                        )
                    }
                    scope.launch {
                        rotationY.animateTo(
                            targetRotationY, 
                            spring(stiffness = Spring.StiffnessMedium)
                        )
                    }

                } while (event.changes.any { it.pressed } && waitForUpOrCancellation() == null)

                // Released or Cancelled - Spring Back
                scope.launch {
                    rotationX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    rotationY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
            }
        }
}
