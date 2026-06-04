package com.fittogether.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkin_records")
data class CheckInRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val categoryId: Int,
    val date: String,          // yyyy-MM-dd
    val value: Float = 0f,     // numeric value (km/min/sets)
    val unit: String = "",     // km, min, sets, kg...
    val note: String = "",
    val mood: String = "",     // emoji mood label
    val photoUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
