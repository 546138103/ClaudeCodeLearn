package com.fittogether.ui.screens.couple

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fittogether.data.model.CheerRecord
import com.fittogether.data.model.User
import com.fittogether.util.FireworksAnimation
import com.fittogether.util.HighFiveAnimation
import com.fittogether.util.HeartParticleEffect
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CoupleSpaceScreen(
    user: User?,
    partner: User?,
    cheerHistory: List<CheerRecord>,
    showHearts: Boolean,
    showHighFive: Boolean,
    showFireworks: Boolean,
    onCheer: (type: String) -> Unit,
    onPoke: () -> Unit,
    onDismissHearts: () -> Unit,
    onDismissHighFive: () -> Unit,
    onDismissFireworks: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A0033),
                            Color(0xFF2D004D),
                            Color(0xFF1A0033)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(top = 48.dp)
            ) {
                // Header
                TextButton(onClick = onBack) {
                    Text("← 返回", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Couple header
                if (user != null && partner != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Dual avatars
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = user.avatar,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        user.nickname.ifBlank { "我" },
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text("❤️", fontSize = 36.sp, modifier = Modifier.padding(horizontal = 16.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = partner.avatar,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        partner.nickname.ifBlank { "Ta" },
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Days together
                            if (user.bindTime > 0) {
                                val days = (System.currentTimeMillis() - user.bindTime) / (24 * 3600 * 1000)
                                Text(
                                    "已在一起 ${days.coerceAtLeast(1)} 天 💕",
                                    color = Color(0xFFFF4081),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CoupleActionButton("👆", "戳一下", onClick = onPoke)
                        CoupleActionButton("❤️", "加油", onClick = { onCheer("cheer") })
                        CoupleActionButton("🔥", "挑战", onClick = { /* challenge dialog */ })
                        CoupleActionButton("🖐️", "击掌", onClick = {
                            onCheer("highfive")
                            onCheer("highfive") // trigger high five animation
                        })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Interaction history
                    Text(
                        "互动记录",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (cheerHistory.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.05f)
                            )
                        ) {
                            Text(
                                "还没有互动哦~ 戳一下Ta吧！",
                                color = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        cheerHistory.take(20).forEach { cheer ->
                            val isFromMe = cheer.fromUserId == user.uid
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.06f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = when (cheer.type) {
                                        "poke" -> "👆"
                                        "cheer" -> "❤️"
                                        "emoji" -> cheer.emoji
                                        else -> "💬"
                                    }
                                    val action = when (cheer.type) {
                                        "poke" -> if (isFromMe) "你戳了Ta一下" else "Ta戳了你一下"
                                        "cheer" -> if (isFromMe) "你为Ta加油" else "Ta为你加油"
                                        else -> if (isFromMe) "你发送了" else "Ta发送了"
                                    }
                                    Text(icon, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        action,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        formatTime(cheer.timestamp),
                                        color = Color.White.copy(alpha = 0.4f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                } else {
                    // No partner yet
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💝", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "还没有绑定健身搭档",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "去设置中绑定Ta，\n一起享受健身的乐趣！",
                                color = Color.White.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Animations
        HeartParticleEffect(isActive = showHearts, onFinished = onDismissHearts)
        HighFiveAnimation(isActive = showHighFive, onFinished = onDismissHighFive)
        FireworksAnimation(isActive = showFireworks, onFinished = onDismissFireworks)
    }
}

@Composable
private fun CoupleActionButton(emoji: String, label: String, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .padding(12.dp)
    ) {
        Text(emoji, fontSize = 32.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

private fun formatTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val time = instant.atZone(ZoneId.systemDefault())
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
}
