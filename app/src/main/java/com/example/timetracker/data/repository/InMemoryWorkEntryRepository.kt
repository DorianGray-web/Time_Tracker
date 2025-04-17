package com.example.timetracker.data.repository

import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.repository.WorkEntryRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryWorkEntryRepository : WorkEntryRepository {
    private val _entries = MutableStateFlow<List<WorkEntry>>(emptyList())
    private var nextId: Long = 1

    override suspend fun getAllEntries(): List<WorkEntry> {
        return _entries.value
    }

    override suspend fun getEntriesForWeek(weekStart: LocalDate): List<WorkEntry> {
        val weekEnd = weekStart.plusDays(6)
        return _entries.value.filter { entry ->
            entry.startTime.toLocalDate() >= weekStart &&
            entry.endTime.toLocalDate() <= weekEnd
        }
    }

    override suspend fun addEntry(entry: WorkEntry) {
        val newEntry = entry.copy(id = nextId++)
        _entries.value = _entries.value + newEntry
    }

    override suspend fun updateEntry(entry: WorkEntry) {
        _entries.value = _entries.value.map { existingEntry ->
            if (existingEntry.id == entry.id) entry else existingEntry
        }
    }

    override suspend fun deleteEntry(entryId: Long) {
        _entries.value = _entries.value.filter { it.id != entryId }
    }
} 