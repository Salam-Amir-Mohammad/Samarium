package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.BatchDetailActivity

@Database(entities = [NetworkInfo::class], version = 2)
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
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // اجرای دستورات SQL برای اضافه کردن ستون batchId
                database.execSQL("ALTER TABLE network_info ADD COLUMN batchId INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}