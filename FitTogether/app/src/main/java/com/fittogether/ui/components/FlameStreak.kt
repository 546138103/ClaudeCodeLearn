package com.fittogether.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlameStreak(
    days: Int,
    modifier: Modifier = Modifier
) {
    val flameScale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isActive = days > 0

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isActive) "🔥" else "💤",
            fontSize = if (isActive) (36 * flameScale).sp else 28.sp,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isActive) "连续 $days 天" else "今天还没打卡",
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive)
                Color(0xFFFF6B35)
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        if (isActive) {
            Text(
                text = buildString {
                    repeat(days.coerceAtMost(30)) { append("🔥") }
                },
                fontSize = 10.sp,
                lineHeight = 12.sp,
                maxLines = 2
            )
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
