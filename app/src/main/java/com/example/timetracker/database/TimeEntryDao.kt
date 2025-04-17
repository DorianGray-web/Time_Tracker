package com.example.timetracker.database

import androidx.room.*
import com.example.timetracker.model.TimeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEntryDao {
    @Query("SELECT * FROM time_entries ORDER BY startTime DESC")
    fun getAllTimeEntries(): Flow<List<TimeEntry>>

    @Query("SELECT * FROM time_entries WHERE id = :id")
    suspend fun getTimeEntryById(id: Long): TimeEntry?

    @Query("SELECT * FROM time_entries WHERE isRunning = 1 LIMIT 1")
    suspend fun getRunningTimeEntry(): TimeEntry?

    @Insert
    suspend fun insertTimeEntry(timeEntry: TimeEntry): Long

    @Update
    suspend fun updateTimeEntry(timeEntry: TimeEntry)

    @Delete
    suspend fun deleteTimeEntry(timeEntry: TimeEntry)

    @Query("DELETE FROM time_entries")
    suspend fun deleteAllTimeEntries()
} 