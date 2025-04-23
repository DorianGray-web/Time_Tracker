package com.example.timetracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.timetracker.data.local.dao.RateSettingsDao
import com.example.timetracker.data.local.dao.UserSettingsDao
import com.example.timetracker.data.local.dao.WorkEntryDao
import com.example.timetracker.data.local.dao.WorkTypeDao
import com.example.timetracker.data.local.entity.RateSettings
import com.example.timetracker.data.local.entity.UserSettings
import com.example.timetracker.data.local.entity.WorkEntryEntity
import com.example.timetracker.data.local.entity.WorkType

@Database(
    entities = [
        WorkEntryEntity::class,
        WorkType::class,
        UserSettings::class,
        RateSettings::class
    ],
    version = 4
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workEntryDao(): WorkEntryDao
    abstract fun workTypeDao(): WorkTypeDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun rateSettingsDao(): RateSettingsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop existing table if it exists
                database.execSQL("DROP TABLE IF EXISTS user_settings")

                // Create new user_settings table with proper schema
                database.execSQL("""
                    CREATE TABLE user_settings (
                        id INTEGER PRIMARY KEY NOT NULL,
                        weeklyHours INTEGER NOT NULL DEFAULT 40,
                        overtimeThreshold INTEGER NOT NULL DEFAULT 8,
                        overtimeMultiplier REAL NOT NULL DEFAULT 1.5,
                        language TEXT NOT NULL DEFAULT 'en',
                        currency TEXT NOT NULL DEFAULT 'EUR',
                        defaultWorkType TEXT,
                        notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                        darkMode INTEGER NOT NULL DEFAULT 0,
                        autoBackup INTEGER NOT NULL DEFAULT 1,
                        backupFrequency INTEGER NOT NULL DEFAULT 7,
                        lastBackupDate INTEGER
                    )
                """.trimIndent())

                // Insert default settings
                database.execSQL("""
                    INSERT INTO user_settings (
                        id, weeklyHours, overtimeThreshold, overtimeMultiplier,
                        language, currency, defaultWorkType, notificationsEnabled,
                        darkMode, autoBackup, backupFrequency, lastBackupDate
                    ) VALUES (
                        1, 40, 8, 1.5, 'en', 'EUR', NULL, 1, 0, 1, 7, NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Verify and fix any potential issues with the user_settings table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_settings_new (
                        id INTEGER PRIMARY KEY NOT NULL,
                        weeklyHours INTEGER NOT NULL DEFAULT 40,
                        overtimeThreshold INTEGER NOT NULL DEFAULT 8,
                        overtimeMultiplier REAL NOT NULL DEFAULT 1.5,
                        language TEXT NOT NULL DEFAULT 'en',
                        currency TEXT NOT NULL DEFAULT 'EUR',
                        defaultWorkType TEXT,
                        notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                        darkMode INTEGER NOT NULL DEFAULT 0,
                        autoBackup INTEGER NOT NULL DEFAULT 1,
                        backupFrequency INTEGER NOT NULL DEFAULT 7,
                        lastBackupDate INTEGER
                    )
                """.trimIndent())

                // Copy data from old table to new
                database.execSQL("""
                    INSERT INTO user_settings_new 
                    SELECT * FROM user_settings
                """.trimIndent())

                // Drop old table and rename new one
                database.execSQL("DROP TABLE user_settings")
                database.execSQL("ALTER TABLE user_settings_new RENAME TO user_settings")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add date column to work_entries table
                database.execSQL("ALTER TABLE work_entries ADD COLUMN date INTEGER NOT NULL DEFAULT 0")
                
                // Update existing entries to use the current date
                database.execSQL("""
                    UPDATE work_entries 
                    SET date = CAST(strftime('%s', 'now') AS INTEGER)
                """.trimIndent())
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "time_tracker_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 