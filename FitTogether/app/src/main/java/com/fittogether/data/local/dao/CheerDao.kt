package com.fittogether.data.local.dao

import androidx.room.*
import com.fittogether.data.model.CheerRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CheerDao {
    @Query("SELECT * FROM cheer_records WHERE (fromUserId = :userId1 AND toUserId = :userId2) OR (fromUserId = :userId2 AND toUserId = :userId1) ORDER BY timestamp DESC")
    fun observeCheerHistory(userId1: String, userId2: String): Flow<List<CheerRecord>>

    @Query("SELECT * FROM cheer_records WHERE toUserId = :userId AND timestamp > :since ORDER BY timestamp DESC")
    fun observeNewCheers(userId: String, since: Long): Flow<List<CheerRecord>>

    @Insert
    suspend fun insertCheer(cheer: CheerRecord)

    @Query("SELECT COUNT(*) FROM cheer_records WHERE toUserId = :userId AND timestamp > :since")
    suspend fun getNewCheerCount(userId: String, since: Long): Int
}
