package com.fittogether.data.local.dao

import androidx.room.*
import com.fittogether.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUser(uid: String): User?

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun observeUser(uid: String): Flow<User?>

    @Query("SELECT * FROM users WHERE partnerId = :partnerId LIMIT 1")
    suspend fun getPartner(partnerId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET partnerId = :partnerId, bindTime = :bindTime WHERE uid = :uid")
    suspend fun bindPartner(uid: String, partnerId: String, bindTime: Long)

    @Query("UPDATE users SET nickname = :nickname, avatar = :avatar, signature = :signature, weeklyGoal = :weeklyGoal WHERE uid = :uid")
    suspend fun updateProfile(uid: String, nickname: String, avatar: String, signature: String, weeklyGoal: Int)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
