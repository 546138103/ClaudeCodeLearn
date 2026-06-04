package com.fittogether.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkin_categories")
data class CheckInCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String,
    val color: Long, // ARGB color as Long
    val isCustom: Boolean = false,
    val userId: String = "" // empty = preset, otherwise belongs to user
)
