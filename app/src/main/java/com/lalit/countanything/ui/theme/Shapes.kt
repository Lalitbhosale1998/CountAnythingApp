// C:/Users/lalit/Documents/App/app/src/main/java/com/lalit/countanything/Shapes.kt
package com.lalit.countanything

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// A list of all our custom shapes to easily pick a random one
val expressiveShapes = listOf(
    SlantedShape,
    ArchShape,
    PillShape,
    GemShape,
    SunnyShape,
    BurstShape,
    FlowerShape,
    HeartShape
)

object SlantedShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val slant = size.width * 0.2f
            moveTo(slant, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width - slant, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

object ArchShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height / 2)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(size.width, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

object PillShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cornerRadius = min(size.width, size.height) / 2f
        val path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(offset = androidx.compose.ui.geometry.Offset.Zero, size = size),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                )
            )
        }
        return Outline.Generic(path)
    }
}


object GemShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                moveTo(size.width * .5f, 0f)
                lineTo(size.width, size.height * .33f)
                lineTo(size.width * .8f, size.height)
                lineTo(size.width * .2f, size.height)
                lineTo(0f, size.height * .33f)
                close()
            }
        )
    }
}

object SunnyShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val radius = size.minDimension / 2
                val innerRadius = radius * 0.6f
                val numPetals = 8
                val angleStep = 360.0 / (numPetals * 2)

                for (i in 0 until numPetals * 2) {
                    val angle = Math.toRadians(i * angleStep)
                    val currentRadius = if (i % 2 == 0) radius else innerRadius
                    val x = size.width / 2 + (currentRadius * cos(angle)).toFloat()
                    val y = size.height / 2 + (currentRadius * sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
        )
    }
}

object BurstShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val outerRadius = size.minDimension / 2f
                val innerRadius = outerRadius / 2f
                val numPoints = 12
                val angleStep = (2 * Math.PI / numPoints).toFloat()

                for (i in 0 until numPoints) {
                    val currentAngle = i * angleStep
                    val nextAngle = (i + 0.5f) * angleStep

                    val outerX = size.width / 2 + cos(currentAngle.toDouble()).toFloat() * outerRadius
                    val outerY = size.height / 2 + sin(currentAngle.toDouble()).toFloat() * outerRadius

                    val innerX = size.width / 2 + cos(nextAngle.toDouble()).toFloat() * innerRadius
                    val innerY = size.height / 2 + sin(nextAngle.toDouble()).toFloat() * innerRadius

                    if (i == 0) moveTo(outerX, outerY) else lineTo(outerX, outerY)
                    lineTo(innerX, innerY)
                }
                close()
            }
        )
    }
}

object FlowerShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            Path().apply {
                val numPetals = 6
                val radius = size.minDimension / 2
                val petalRadius = radius / 2f
                val angleStep = 360f / numPetals

                for (i in 0 until numPetals) {
                    val angle = Math.toRadians((i * angleStep).toDouble()).toFloat()
                    val cx = size.width / 2 + (radius - petalRadius) * cos(angle)
                    val cy = size.height / 2 + (radius - petalRadius) * sin(angle)
                    addOval(androidx.compose.ui.geometry.Rect(cx - petalRadius, cy - petalRadius, cx + petalRadius, cy + petalRadius))
                }
            }
        )
    }
}

object HeartShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            moveTo(width / 2, height * 0.4f)
            cubicTo(
                width * 0.2f, height * 0.1f,
                -width * 0.2f, height * 0.6f,
                width / 2, height
            )
            cubicTo(
                width * 1.2f, height * 0.6f,
                width * 0.8f, height * 0.1f,
                width / 2, height * 0.4f
            )
            close()
        }
        return Outline.Generic(path)
    }
}
