package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NetworkInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun networkInfoDao(): NetworkInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "network_info_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
