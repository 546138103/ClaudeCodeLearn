package com.fittogether.ui.screens.login

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLogin: (phone: String) -> Unit,
    onRegister: (phone: String, nickname: String) -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7C4DFF),
                        Color(0xFF448AFF),
                        Color(0xFF00BCD4)
                    )
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
            Text("💑", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "FitTogether",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Phone input
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.take(11) },
                label = { Text("手机号", color = Color.White.copy(alpha = 0.7f)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            // Nickname for registration
            AnimatedVisibility(
                visible = isRegister,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("昵称", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login/Register button
            Button(
                onClick = {
                    if (phone.length >= 11) {
                        if (isRegister && nickname.isNotBlank()) {
                            onRegister(phone, nickname)
                        } else if (!isRegister) {
                            onLogin(phone)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = phone.length >= 11
            ) {
                Text(
                    text = if (isRegister) "注册并登录" else "登录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C4DFF)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isRegister = !isRegister }) {
                Text(
                    text = if (isRegister) "已有账号？去登录" else "没有账号？去注册",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}
