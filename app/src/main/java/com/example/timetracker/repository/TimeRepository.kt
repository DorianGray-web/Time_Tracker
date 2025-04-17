package com.example.timetracker.repository

import android.content.Context
import com.example.timetracker.database.AppDatabase
import com.example.timetracker.database.TimeEntryDao
import com.example.timetracker.model.TimeEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class TimeRepository(
    private val context: Context,
    private val timeEntryDao: TimeEntryDao
) {
    fun getAllTimeEntries(): Flow<List<TimeEntry>> {
        return timeEntryDao.getAllTimeEntries()
    }
    
    suspend fun startTimer(description: String) {
        val newEntry = TimeEntry(
            startTime = LocalDateTime.now(),
            description = description,
            isRunning = true
        )
        timeEntryDao.insertTimeEntry(newEntry)
    }
    
    suspend fun stopTimer(entryId: Long) {
        val entry = timeEntryDao.getRunningTimer()
        entry?.let {
            timeEntryDao.updateTimeEntry(it.copy(
                endTime = LocalDateTime.now(),
                isRunning = false
            ))
        }
    }
} 