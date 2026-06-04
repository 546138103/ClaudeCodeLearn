package com.fittogether.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val color: Color,
    val size: Float,
    val speed: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val horizontalDrift: Float
)

private val confettiColors = listOf(
    Color(0xFFFF6B6B), Color(0xFFFF8E53), Color(0xFF7C4DFF),
    Color(0xFF448AFF), Color(0xFF00E676), Color(0xFFFFEA00),
    Color(0xFFFF4081), Color(0xFF40C4FF), Color(0xFFB2FF59),
)

@Composable
fun ConfettiAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 80,
    onFinished: () -> Unit = {}
) {
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * -0.5f,
                color = confettiColors.random(),
                size = Random.nextFloat() * 12f + 4f,
                speed = Random.nextFloat() * 0.5f + 0.3f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                horizontalDrift = Random.nextFloat() * 0.1f - 0.05f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            progress.snapTo(0f)
            while (true) {
                progress.animateTo(
                    2f,
                    animationSpec = tween(3000, easing = LinearEasing)
                )
                progress.snapTo(0f)
            }
        } else {
            onFinished()
        }
    }

    if (isActive) {
        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { particle ->
                val p = progress.value
                val y = (particle.startY + p * particle.speed) * size.height
                val x = particle.x * size.width + sin(p * 2 + particle.x * 10) * particle.horizontalDrift * size.width
                val alpha = when {
                    p < 0.1f -> p / 0.1f
                    p > 1.5f -> 1f - (p - 1.5f) / 0.5f
                    else -> 1f
                }.coerceIn(0f, 1f)

                rotate(degrees = particle.rotation + particle.rotationSpeed * p, pivot = Offset(x, y)) {
                    drawRect(
                        color = particle.color.copy(alpha = alpha),
                        topLeft = Offset(x - particle.size / 2, y - particle.size / 2),
                        size = Size(particle.size, particle.size * 0.6f)
                    )
                }
            }
        }
    }
}

private fun sin(value: Float): Float = kotlin.math.sin(value.toDouble()).toFloat()
