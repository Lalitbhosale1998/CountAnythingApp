package com.lalit.countanything.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
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
            awaitPointerEventScope {
                while (true) {
                    // Use Final pass to allow children (PulseButtons) to handle events first
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    val change = event.changes.firstOrNull() ?: continue
                    
                    if (change.pressed) {
                        val width = size.width.toFloat()
                        val height = size.height.toFloat()
                        val centerX = width / 2f
                        val centerY = height / 2f
                        
                        // We use the position even if consumed by children
                        val normalizeX = (change.position.x - centerX) / centerX
                        val normalizeY = (change.position.y - centerY) / centerY

                        scope.launch {
                            scale.animateTo(scaleOnPress, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            rotationX.animateTo(-normalizeY * maxRotation * 2f, spring(stiffness = Spring.StiffnessMedium))
                            rotationY.animateTo(normalizeX * maxRotation * 2f, spring(stiffness = Spring.StiffnessMedium))
                        }
                    } else {
                        // All fingers released - spring back
                        scope.launch {
                            rotationX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            rotationY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                    }
                }
            }
        }




}
