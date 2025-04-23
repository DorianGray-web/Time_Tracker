package com.example.timetracker.domain.repository

import com.example.timetracker.domain.model.FilterType
import com.example.timetracker.domain.model.WorkEntry
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface WorkEntryRepository {
    fun getAllEntries(): Flow<List<WorkEntry>>
    suspend fun getEntriesForWeek(weekStart: LocalDate): List<WorkEntry>
    suspend fun addEntry(entry: WorkEntry)
    suspend fun updateEntry(entry: WorkEntry)
    suspend fun deleteEntry(entryId: Long)
    suspend fun getEntry(entryId: Long): WorkEntry?
    suspend fun getFilteredEntries(filter: FilterType): List<WorkEntry>
} 