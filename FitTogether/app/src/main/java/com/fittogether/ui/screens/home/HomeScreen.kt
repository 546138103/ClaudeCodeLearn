package com.fittogether.ui.screens.home

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittogether.data.model.CheckInCategory
import com.fittogether.data.model.CheckInRecord
import com.fittogether.data.model.User

@Composable
fun HomeScreen(
    user: User?,
    partner: User?,
    categories: List<CheckInCategory>,
    todayRecords: List<CheckInRecord>,
    showConfetti: Boolean,
    showHeartParticles: Boolean,
    streakDays: Int,
    weekProgress: Float,
    onCategoryClick: (CheckInCategory) -> Unit,
    onDismissConfetti: () -> Unit,
    onDismissHeartParticles: () -> Unit,
    onCheerClick: (type: String) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCouple: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1A2E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(top = 60.dp)
        ) {
            // Header
            Text(
                "FitTogether",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Hi, ${user?.nickname?.ifBlank { "健身达人" } ?: "健身达人"}",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", fontSize = 32.sp)
                        Text("连续 ${streakDays} 天", color = Color.White, fontSize = 14.sp)
                    }
                    // Simple separator instead of Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(60.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 32.sp)
                        Text("完成 ${(weekProgress * 100).toInt()}%", color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Partner card
            if (partner != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCouple() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4081).copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💑", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                partner.nickname.ifBlank { "Ta" },
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                "和Ta一起健身吧",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Button(
                            onClick = { onCheerClick("cheer") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                        ) {
                            Text("❤️ 加油")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Category grid
            Text(
                "快速打卡",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Simple grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { cat ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clickable { onCategoryClick(cat) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(cat.color).copy(alpha = 0.15f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(cat.icon, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        cat.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(cat.color)
                                    )
                                }
                            }
                        }
                        // Fill empty slots
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Today's records
            Text(
                "今日打卡",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (todayRecords.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Text(
                        "今天还没有打卡哦~\n选一个类别开始吧！",
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        fontSize = 14.sp
                    )
                }
            } else {
                todayRecords.forEach { record ->
                    val cat = categories.find { it.id == record.categoryId }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat?.icon ?: "✅", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    cat?.name ?: "打卡",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                if (record.value > 0) {
                                    Text(
                                        "${record.value}${record.unit}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            if (record.mood.isNotEmpty()) {
                                Text(record.mood, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
