package com.fittogether.ui.screens.checkin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittogether.data.model.CheckInCategory
import com.fittogether.ui.components.GlassCard
import com.fittogether.ui.components.GradientBackground
import com.fittogether.ui.components.RippleButton
import java.time.LocalTime

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    category: CheckInCategory,
    onCheckIn: (value: Float, unit: String, note: String, mood: String, photoUrl: String) -> Unit,
    onBack: () -> Unit
) {
    var value by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("分钟") }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("😊") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> photoUri = uri }

    val units = listOf("分钟", "公里", "组", "次", "kg")

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(hourOfDay = LocalTime.now().hour) {
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
                    Text(
                        "${category.icon} ${category.name}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Value input
                GlassCard {
                    Text("训练量", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { value = it.filter { c -> c.isDigit() || c == '.' } },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("输入数值", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Unit selector
                        units.forEach { u ->
                            FilterChip(
                                selected = unit == u,
                                onClick = { unit = u },
                                label = { Text(u, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(category.color),
                                    selectedLabelColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Photo
                GlassCard {
                    Text("拍照记录", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            if (photoUri != null) "📸 已选择照片" else "📷 点击拍照或选照片",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                GlassCard {
                    Text("备注", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                        placeholder = { Text("今天练得怎么样？", color = Color.White.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mood selector
                GlassCard {
                    Text("心情", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("😊" to "轻松", "😅" to "一般", "🥵" to "累爆了", "💪" to "有力量", "🔥" to "燃脂").forEach { (emoji, label) ->
                            FilterChip(
                                selected = selectedMood == emoji,
                                onClick = { selectedMood = emoji },
                                label = { Text("$emoji $label", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(category.color),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit
                RippleButton(
                    text = "✅ 完成打卡",
                    onClick = {
                        val v = value.toFloatOrNull() ?: 0f
                        onCheckIn(v, unit, note, selectedMood, photoUri?.toString() ?: "")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    gradientColors = listOf(
                        Color(category.color),
                        Color(category.color).copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
