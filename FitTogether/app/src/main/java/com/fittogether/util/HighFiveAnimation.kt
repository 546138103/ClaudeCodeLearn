package com.fittogether.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun HighFiveAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            progress.snapTo(0f)
            progress.animateTo(
                1f,
                animationSpec = tween(800, easing = EaseOutBack)
            )
            onFinished()
        }
    }

    if (isActive) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val spacing = 60f * (1f + (1f - progress.value) * 0.5f)

            // Left hand
            val leftX = cx - spacing * progress.value
            drawHand(Offset(leftX, cy), size = 60f, color = CouplePink, flipped = true)

            // Right hand
            val rightX = cx + spacing * progress.value
            drawHand(Offset(rightX, cy), size = 60f, color = CoupleBlue, flipped = false)

            // Impact spark
            if (progress.value > 0.75f) {
                val sparkAlpha = ((progress.value - 0.75f) / 0.25f).coerceIn(0f, 1f)
                val sparkRadius = 80f * (1f - (progress.value - 0.75f) / 0.25f)
                drawCircle(
                    color = Color.Yellow.copy(alpha = sparkAlpha * 0.6f),
                    radius = sparkRadius.coerceAtLeast(0f),
                    center = Offset(cx, cy - 20f),
                    style = Stroke(width = 3f * sparkAlpha)
                )
                // Rays
                for (i in 0 until 12) {
                    val angle = (i * 30f) * Math.PI.toFloat() / 180f
                    val rayLen = 40f * sparkAlpha
                    drawLine(
                        color = Color.Yellow.copy(alpha = sparkAlpha * 0.8f),
                        start = Offset(cx, cy - 20f),
                        end = Offset(
                            cx + kotlin.math.cos(angle) * rayLen,
                            cy - 20f + kotlin.math.sin(angle) * rayLen
                        ),
                        strokeWidth = 2f * sparkAlpha,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHand(
    center: Offset, size: Float, color: Color, flipped: Boolean
) {
    val dir = if (flipped) 1f else -1f
    // Simple palm
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - size * 0.35f, center.y - size * 0.5f),
        size = Size(size * 0.7f, size),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size * 0.3f)
    )
    // Fingers
    for (i in 0 until 4) {
        drawRoundRect(
            color = color.copy(alpha = 0.8f),
            topLeft = Offset(
                center.x + dir * size * 0.3f + dir * i * size * 0.15f,
                center.y - size * 0.7f
            ),
            size = Size(size * 0.12f, size * 0.4f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size * 0.06f)
        )
    }
}

private val CouplePink = Color(0xFFFF4081)
private val CoupleBlue = Color(0xFF448AFF)
private val EaseOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
