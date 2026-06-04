package com.fittogether.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val phone: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val signature: String = "",
    val partnerId: String? = null,
    val bindTime: Long = 0,
    val weeklyGoal: Int = 5
)
