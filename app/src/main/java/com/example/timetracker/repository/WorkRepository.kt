package com.example.timetracker.repository

import com.example.timetracker.data.WorkEntryDao
import com.example.timetracker.model.WorkEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkRepository @Inject constructor(
    private val workEntryDao: WorkEntryDao
) {
    fun getAllWorkEntries(): Flow<List<WorkEntry>> {
        return workEntryDao.getAllWorkEntries()
    }

    fun getWorkEntriesByDate(date: LocalDate): Flow<List<WorkEntry>> {
        return workEntryDao.getWorkEntriesByDate(date.toString())
    }

    suspend fun insertWorkEntry(entry: WorkEntry) {
        workEntryDao.insert(entry)
    }

    suspend fun deleteWorkEntry(entry: WorkEntry) {
        workEntryDao.delete(entry)
    }

    suspend fun updateWorkEntry(entry: WorkEntry) {
        workEntryDao.update(entry)
    }
} 