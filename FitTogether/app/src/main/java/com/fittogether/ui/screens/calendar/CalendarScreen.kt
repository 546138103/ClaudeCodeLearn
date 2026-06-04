package com.fittogether.ui.screens.calendar

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittogether.data.model.CheckInCategory
import com.fittogether.data.model.CheckInRecord
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(
    monthRecords: List<CheckInRecord>,
    categories: List<CheckInCategory>,
    partnerRecords: List<CheckInRecord> = emptyList(),
    onBack: () -> Unit
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(top = 48.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("← 返回", color = Color.White, fontSize = 16.sp)
                    }
                    Text("打卡日历", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(64.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }) {
                        Text("◀", color = Color.White, fontSize = 18.sp)
                    }
                    Text(
                        currentYearMonth.format(DateTimeFormatter.ofPattern("yyyy年 M月")),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }) {
                        Text("▶", color = Color.White, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day-of-week headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("一", "二", "三", "四", "五", "六", "日").forEach { day ->
                        Text(
                            day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar grid
                val firstDayOfMonth = currentYearMonth.atDay(1)
                val daysInMonth = currentYearMonth.lengthOfMonth()
                val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value - 1) // Mon=0

                val totalCells = firstDayOfWeek + daysInMonth
                val rows = (totalCells + 6) / 7

                val recordsByDate = monthRecords.groupBy { it.date }
                val partnerByDate = partnerRecords.groupBy { it.date }

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOfWeek + 1

                            if (day in 1..daysInMonth) {
                                val date = currentYearMonth.atDay(day)
                                val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                val records = recordsByDate[dateStr] ?: emptyList()
                                val partnerHasRecords = (partnerByDate[dateStr]?.size ?: 0) > 0

                                val bgColor = when {
                                    records.isNotEmpty() && partnerHasRecords -> Color(0xFF7C4DFF)
                                    records.isNotEmpty() -> Color(0xFFFF6B35)
                                    partnerHasRecords -> Color(0xFF448AFF)
                                    else -> Color.Transparent
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(bgColor.copy(alpha = if (bgColor == Color.Transparent) 0f else 0.3f))
                                        .clickable { /* show day details */ },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        fontSize = 14.sp,
                                        color = if (date == LocalDate.now()) Color(0xFFFF4081) else Color.White,
                                        fontWeight = if (date == LocalDate.now()) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (records.isNotEmpty()) {
                                        Text("●", fontSize = 8.sp, color = Color(0xFFFF6B35))
                                    }
                                    if (partnerHasRecords) {
                                        Text("●", fontSize = 8.sp, color = Color(0xFF448AFF))
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Legend
                GlassCardLocal {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem("●", "你的打卡", Color(0xFFFF6B35))
                        LegendItem("●", "Ta的打卡", Color(0xFF448AFF))
                        LegendItem("●", "同时打卡", Color(0xFF7C4DFF))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category summary for month
                val monthStr = currentYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                val monthRecs = monthRecords.filter { it.date.startsWith(monthStr) }
                if (monthRecs.isNotEmpty()) {
                    Text("本月统计", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    val byCategory = monthRecs.groupBy { it.categoryId }
                    GlassCardLocal {
                        byCategory.forEach { (catId, recs) ->
                            val cat = categories.find { it.id == catId }
                            val total = recs.sumOf { it.value.toDouble() }.toFloat()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${cat?.icon ?: "✅"} ${cat?.name ?: "未知"}", color = Color.White)
                                Text("${recs.size}次 · ${total}${recs.first().unit}", color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(dot: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(dot, color = color, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
private fun GlassCardLocal(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(16.dp),
        content = content
    )
}
