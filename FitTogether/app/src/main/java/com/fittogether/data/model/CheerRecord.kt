package com.fittogether.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cheer_records")
data class CheerRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromUserId: String,
    val toUserId: String,
    val type: String,          // "poke", "cheer", "emoji"
    val emoji: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
