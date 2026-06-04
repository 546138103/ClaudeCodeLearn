package com.fittogether.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fittogether.data.local.dao.*
import com.fittogether.data.model.*

@Database(
    entities = [User::class, CheckInCategory::class, CheckInRecord::class, CheerRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recordDao(): RecordDao
    abstract fun cheerDao(): CheerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fittogether.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
