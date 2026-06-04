package com.fittogether.data.repository

import com.fittogether.data.local.AppDatabase
import com.fittogether.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FitRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val recordDao = db.recordDao()
    private val cheerDao = db.cheerDao()

    // ── User ──
    suspend fun getUser(uid: String): User? = userDao.getUser(uid)
    fun observeUser(uid: String): Flow<User?> = userDao.observeUser(uid)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun bindPartner(uid: String, partnerId: String, bindTime: Long = System.currentTimeMillis()) {
        userDao.bindPartner(uid, partnerId, bindTime)
        userDao.bindPartner(partnerId, uid, bindTime)
    }
    suspend fun updateProfile(uid: String, nickname: String, avatar: String, signature: String, weeklyGoal: Int) =
        userDao.updateProfile(uid, nickname, avatar, signature, weeklyGoal)

    // ── Categories ──
    fun observeCategories(userId: String): Flow<List<CheckInCategory>> =
        categoryDao.observeCategories(userId)
    suspend fun getCategories(userId: String): List<CheckInCategory> =
        categoryDao.getCategories(userId)
    suspend fun insertCategory(category: CheckInCategory): Long =
        categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: CheckInCategory) =
        categoryDao.deleteCategory(category)

    // ── Records ──
    fun observeTodayRecords(userId: String, date: String): Flow<List<CheckInRecord>> =
        recordDao.observeTodayRecords(userId, date)
    suspend fun getTodayRecords(userId: String, date: String): List<CheckInRecord> =
        recordDao.getTodayRecords(userId, date)
    fun observeMonthRecords(userId: String, month: String): Flow<List<CheckInRecord>> =
        recordDao.observeMonthRecords(userId, month)
    suspend fun getMonthRecords(userId: String, month: String): List<CheckInRecord> =
        recordDao.getMonthRecords(userId, month)
    suspend fun getRecordByCategoryAndDate(userId: String, categoryId: Int, date: String): CheckInRecord? =
        recordDao.getRecordByCategoryAndDate(userId, categoryId, date)
    suspend fun insertRecord(record: CheckInRecord): Long =
        recordDao.insertRecord(record)
    suspend fun getTotalCheckInDays(userId: String): Int =
        recordDao.getTotalCheckInDays(userId)
    suspend fun getWeekCheckInCount(userId: String): Int {
        val fmt = DateTimeFormatter.ofPattern("yyyy-'W'ww")
        val week = LocalDate.now().format(fmt)
        return recordDao.getWeekCheckInCount(userId, week)
    }

    // ── Cheers ──
    fun observeCheerHistory(userId1: String, userId2: String): Flow<List<CheerRecord>> =
        cheerDao.observeCheerHistory(userId1, userId2)
    suspend fun insertCheer(cheer: CheerRecord) =
        cheerDao.insertCheer(cheer)

    // ── Preset categories ──
    suspend fun ensurePresetCategories() {
        val existing = categoryDao.getCategories("")
        if (existing.isNotEmpty()) return

        val presets = listOf(
            CheckInCategory(name = "跑步", icon = "🏃", color = 0xFFFF6B35, isCustom = false, userId = ""),
            CheckInCategory(name = "练胸", icon = "💪", color = 0xFFE53935, isCustom = false, userId = ""),
            CheckInCategory(name = "练背", icon = "🏋️", color = 0xFF1E88E5, isCustom = false, userId = ""),
            CheckInCategory(name = "练腿", icon = "🦵", color = 0xFF7B1FA2, isCustom = false, userId = ""),
            CheckInCategory(name = "练肩", icon = "🎯", color = 0xFF00897B, isCustom = false, userId = ""),
            CheckInCategory(name = "拉伸", icon = "🧘", color = 0xFF43A047, isCustom = false, userId = ""),
            CheckInCategory(name = "HIIT", icon = "🔥", color = 0xFFD81B60, isCustom = false, userId = ""),
        )
        presets.forEach { categoryDao.insertCategory(it) }
    }
}
