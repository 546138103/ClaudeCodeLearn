package com.fittogether.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.random.Random

data class HeartParticleData(
    val startX: Float,
    val startY: Float,
    val scale: Float,
    val speed: Float,
    val drift: Float,
    val alpha: Float,
    val color: Color
)

@Composable
fun HeartParticleEffect(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    count: Int = 20,
    originX: Float = 0.5f,
    originY: Float = 0.7f,
    onFinished: () -> Unit = {}
) {
    val hearts = remember {
        List(count) {
            HeartParticleData(
                startX = originX + Random.nextFloat() * 0.2f - 0.1f,
                startY = originY + Random.nextFloat() * 0.1f,
                scale = Random.nextFloat() * 0.6f + 0.3f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                drift = Random.nextFloat() * 0.15f - 0.075f,
                alpha = Random.nextFloat() * 0.5f + 0.5f,
                color = Color(
                    red = Random.nextFloat() * 0.3f + 0.7f,
                    green = Random.nextFloat() * 0.2f,
                    blue = Random.nextFloat() * 0.3f + 0.3f,
                )
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            progress.snapTo(0f)
            while (true) {
                progress.animateTo(1.5f, tween(2000, easing = LinearEasing))
                progress.snapTo(0f)
            }
        } else {
            onFinished()
        }
    }

    if (isActive) {
        Canvas(modifier = modifier.fillMaxSize()) {
            hearts.forEach { h ->
                val p = progress.value
                val x = h.startX * size.width + sin(p * 3 + h.startX * 5) * h.drift * size.width
                val y = h.startY * size.height - p * h.speed * size.height
                val alpha = (h.alpha * (1f - p / 1.5f)).coerceIn(0f, 1f)
                drawHeart(
                    center = Offset(x, y),
                    sz = 20f * h.scale,
                    color = h.color.copy(alpha = alpha)
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeart(
    center: Offset,
    sz: Float,
    color: Color
) {
    val path = Path().apply {
        val s = sz / 2
        moveTo(center.x, center.y + s * 0.6f)
        cubicTo(center.x - s, center.y - s * 0.3f, center.x - s * 0.5f, center.y - s * 1.2f, center.x, center.y - s * 0.6f)
        cubicTo(center.x + s * 0.5f, center.y - s * 1.2f, center.x + s, center.y - s * 0.3f, center.x, center.y + s * 0.6f)
        close()
    }
    drawPath(path, color)
}

private fun sin(v: Float): Float = kotlin.math.sin(v.toDouble()).toFloat()
