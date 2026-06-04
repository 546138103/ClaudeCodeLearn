package com.fittogether.ui.screens.bind

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BindScreen(
    onBind: (inviteCode: String) -> Unit,
    onSkip: () -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }
    val pulse = rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutCubic), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF4081), Color(0xFFFF6B6B), Color(0xFFFF8E53))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated hearts
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("❤️", fontSize = (40 * pulse.value).sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("💑", fontSize = 56.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("❤️", fontSize = (40 * pulse.value).sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "连接你和 Ta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "输入邀请码来绑定你的健身搭档\n绑定后可以互相加油、共享打卡动态",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = inviteCode,
                onValueChange = { inviteCode = it.take(6) },
                label = { Text("邀请码", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { if (inviteCode.length >= 4) onBind(inviteCode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = inviteCode.length >= 4
            ) {
                Text("绑定", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4081))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSkip) {
                Text("先跳过，稍后再说", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
