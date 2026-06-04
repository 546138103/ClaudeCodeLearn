package com.fittogether.data.local.dao

import androidx.room.*
import com.fittogether.data.model.CheckInCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM checkin_categories WHERE userId = '' OR userId = :userId ORDER BY isCustom ASC, id ASC")
    fun observeCategories(userId: String): Flow<List<CheckInCategory>>

    @Query("SELECT * FROM checkin_categories WHERE userId = '' OR userId = :userId ORDER BY isCustom ASC, id ASC")
    suspend fun getCategories(userId: String): List<CheckInCategory>

    @Query("SELECT * FROM checkin_categories WHERE id = :id")
    suspend fun getCategory(id: Int): CheckInCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CheckInCategory): Long

    @Update
    suspend fun updateCategory(category: CheckInCategory)

    @Delete
    suspend fun deleteCategory(category: CheckInCategory)

    @Query("DELETE FROM checkin_categories WHERE isCustom = 1 AND userId = :userId")
    suspend fun deleteCustomCategories(userId: String)
}
