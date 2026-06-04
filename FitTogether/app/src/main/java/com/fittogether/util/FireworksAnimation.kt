package com.fittogether.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Spark(
    val angle: Float,
    val speed: Float,
    val radius: Float,
    val color: Color,
    val size: Float
)

@Composable
fun FireworksAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    val bursts = remember {
        List(3) {
            val cx = Random.nextFloat() * 0.6f + 0.2f
            val cy = Random.nextFloat() * 0.4f + 0.1f
            val sparkCount = Random.nextInt(20, 35)
            val sparks = List(sparkCount) {
                Spark(
                    angle = Random.nextFloat() * 360f,
                    speed = Random.nextFloat() * 0.5f + 0.3f,
                    radius = Random.nextFloat() * 3f + 1f,
                    color = Color(
                        red = Random.nextFloat() * 0.7f + 0.3f,
                        green = Random.nextFloat() * 0.7f + 0.3f,
                        blue = Random.nextFloat() * 0.7f + 0.3f
                    ),
                    size = Random.nextFloat() * 4f + 2f
                )
            }
            sparks to Offset(cx, cy)
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            progress.snapTo(0f)
            while (true) {
                progress.animateTo(1f, tween(1500, easing = LinearEasing))
                progress.snapTo(0f)
            }
        } else {
            onFinished()
        }
    }

    if (isActive) {
        Canvas(modifier = modifier.fillMaxSize()) {
            bursts.forEach { (sparks, center) ->
                val cx = center.x * size.width
                val cy = center.y * size.height
                sparks.forEach { spark ->
                    val p = progress.value
                    val r = spark.radius * p * size.width * 0.3f
                    val x = cx + r * cos(Math.toRadians(spark.angle.toDouble())).toFloat()
                    val y = cy + r * sin(Math.toRadians(spark.angle.toDouble())).toFloat()
                    val alpha = (1f - p).coerceIn(0f, 1f)
                    drawCircle(spark.color.copy(alpha = alpha), spark.size, Offset(x, y))
                }
            }
        }
    }
}
