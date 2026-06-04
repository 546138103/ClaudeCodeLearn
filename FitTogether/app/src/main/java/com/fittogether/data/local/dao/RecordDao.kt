package com.fittogether.data.local.dao

import androidx.room.*
import com.fittogether.data.model.CheckInRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM checkin_records WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun observeTodayRecords(userId: String, date: String): Flow<List<CheckInRecord>>

    @Query("SELECT * FROM checkin_records WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getTodayRecords(userId: String, date: String): List<CheckInRecord>

    @Query("SELECT * FROM checkin_records WHERE userId = :userId AND strftime('%Y-%m', date) = :month ORDER BY date DESC")
    fun observeMonthRecords(userId: String, month: String): Flow<List<CheckInRecord>>

    @Query("SELECT * FROM checkin_records WHERE userId = :userId AND strftime('%Y-%m', date) = :month ORDER BY date DESC")
    suspend fun getMonthRecords(userId: String, month: String): List<CheckInRecord>

    @Query("SELECT * FROM checkin_records WHERE userId = :userId AND categoryId = :categoryId AND date = :date LIMIT 1")
    suspend fun getRecordByCategoryAndDate(userId: String, categoryId: Int, date: String): CheckInRecord?

    @Query("SELECT COUNT(DISTINCT date) FROM checkin_records WHERE userId = :userId")
    suspend fun getTotalCheckInDays(userId: String): Int

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT date FROM checkin_records
            WHERE userId = :userId
            GROUP BY date
            ORDER BY date DESC
        )
    """)
    suspend fun getConsecutiveDays(userId: String): Int

    @Query("SELECT COUNT(*) FROM checkin_records WHERE userId = :userId AND strftime('%Y-%W', date) = :week")
    suspend fun getWeekCheckInCount(userId: String, week: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CheckInRecord): Long

    @Update
    suspend fun updateRecord(record: CheckInRecord)

    @Delete
    suspend fun deleteRecord(record: CheckInRecord)
}
