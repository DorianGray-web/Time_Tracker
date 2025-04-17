package com.example.timetracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timetracker.data.WorkEntryDao
import com.example.timetracker.database.TimeEntryDao
import com.example.timetracker.model.TimeEntry
import com.example.timetracker.model.WorkEntry

@Database(
    entities = [TimeEntry::class, WorkEntry::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class TimeTrackerDatabase : RoomDatabase() {
    abstract fun timeEntryDao(): TimeEntryDao
    abstract fun workEntryDao(): WorkEntryDao

    companion object {
        const val DATABASE_NAME = "time_tracker_db"
    }
} 