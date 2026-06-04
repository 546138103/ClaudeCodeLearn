package com.fittogether.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fittogether.data.model.CheckInCategory
import com.fittogether.data.model.User
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    categories: List<CheckInCategory>,
    totalDays: Int,
    weekCount: Int,
    onUpdateProfile: (nickname: String, avatar: String, signature: String, weeklyGoal: Int) -> Unit,
    onAddCategory: (name: String, icon: String, color: Long) -> Unit,
    onDeleteCategory: (CheckInCategory) -> Unit,
    onBack: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editNickname by remember { mutableStateOf(user?.nickname ?: "") }
    var editSignature by remember { mutableStateOf(user?.signature ?: "") }
    var editGoal by remember { mutableStateOf(user?.weeklyGoal?.toString() ?: "5") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> avatarUri = uri }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A2E), Color(0xFF162447), Color(0xFF1A1A2E))
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
                TextButton(onClick = onBack) {
                    Text("← 返回", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar & name
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarUri != null || user?.avatar?.isNotBlank() == true) {
                            AsyncImage(
                                model = avatarUri ?: user?.avatar,
                                contentDescription = "avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("📷", fontSize = 40.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        user?.nickname?.ifBlank { "健身达人" } ?: "未登录",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        user?.signature?.ifBlank { "这个人很懒，什么都没写" } ?: "",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            editNickname = user?.nickname ?: ""
                            editSignature = user?.signature ?: ""
                            editGoal = user?.weeklyGoal?.toString() ?: "5"
                            showEditDialog = true
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("✏️ 编辑资料", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard("📅", "${totalDays}", "累计打卡天")
                    StatCard("✅", "${weekCount}", "本周打卡")
                    StatCard("🎯", "${user?.weeklyGoal ?: 5}", "每周目标")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom categories management
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "自定义类别",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    TextButton(onClick = { showAddCategoryDialog = true }) {
                        Text("+ 添加", color = Color(0xFF7C4DFF))
                    }
                }

                val customCats = categories.filter { it.isCustom }
                if (customCats.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        )
                    ) {
                        Text(
                            "还没有自定义类别\n点击右上角添加",
                            color = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    customCats.forEach { cat ->
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
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(cat.name, color = Color.White, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { onDeleteCategory(cat) }) {
                                    Text("删除", color = Color(0xFFE53935))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings
                Text("设置", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                SettingItem("🔔", "提醒时间", "每天 19:00")
                SettingItem("🎨", "主题", "跟随系统")
                SettingItem("ℹ️", "关于", "FitTogether v1.0.0")

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("编辑资料") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = { editNickname = it },
                        label = { Text("昵称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editSignature,
                        onValueChange = { editSignature = it },
                        label = { Text("个性签名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editGoal,
                        onValueChange = { editGoal = it.filter { c -> c.isDigit() } },
                        label = { Text("每周打卡目标") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateProfile(
                        editNickname,
                        avatarUri?.toString() ?: user?.avatar ?: "",
                        editSignature,
                        editGoal.toIntOrNull() ?: 5
                    )
                    showEditDialog = false
                }) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("取消") }
            }
        )
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        var newName by remember { mutableStateOf("") }
        var newIcon by remember { mutableStateOf("🏋️") }
        val iconOptions = listOf("🏋️", "🚴", "🏊", "🧘", "⚽", "🏀", "🎾", "🥊", "⛰️", "💃")

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("添加自定义类别") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("类别名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("选择图标", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconOptions.take(5).forEach { icon ->
                            FilterChip(
                                selected = newIcon == icon,
                                onClick = { newIcon = icon },
                                label = { Text(icon, fontSize = 20.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconOptions.drop(5).forEach { icon ->
                            FilterChip(
                                selected = newIcon == icon,
                                onClick = { newIcon = icon },
                                label = { Text(icon, fontSize = 20.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        onAddCategory(newName, newIcon, 0xFF7C4DFF)
                        showAddCategoryDialog = false
                    }
                }) { Text("添加") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun SettingItem(icon: String, title: String, subtitle: String) {
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = Color.White, modifier = Modifier.weight(1f))
            Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
        }
    }
}
