package com.fittogether.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    blurRadius: Dp = 10.dp,
    gradientColors: List<Color> = listOf(
        Color.White.copy(alpha = 0.25f),
        Color.White.copy(alpha = 0.1f)
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(20.dp),
        content = content
    )
}

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    hourOfDay: Int = 12,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = if (isDark) {
        // Night gradient
        listOf(Color(0xFF0F0F23), Color(0xFF1A1030), Color(0xFF0D1B2A))
    } else {
        when (hourOfDay) {
            in 5..9 -> listOf(Color(0xFFFFD89B), Color(0xFFFF8A80), Color(0xFFFFAB91)) // Morning warm
            in 10..16 -> listOf(Color(0xFF81D4FA), Color(0xFF64B5F6), Color(0xFF42A5F5)) // Day blue
            in 17..19 -> listOf(Color(0xFFFFAB91), Color(0xFFCE93D8), Color(0xFF90CAF9)) // Sunset
            else -> listOf(Color(0xFF1A237E), Color(0xFF4A148C), Color(0xFF311B92)) // Night
        }
    }

    Box(
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        ),
        content = content
    )
}
