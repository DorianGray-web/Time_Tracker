package com.example.timetracker.data.repository

import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.repository.WorkEntryRepository
import com.example.timetracker.domain.model.FilterType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryWorkEntryRepository : WorkEntryRepository {
    private val _entries = MutableStateFlow<List<WorkEntry>>(emptyList())
    private var nextId: Long = 1

    override fun getAllEntries(): Flow<List<WorkEntry>> = _entries.asStateFlow()

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

    override suspend fun getEntry(entryId: Long): WorkEntry? {
        return _entries.value.find { it.id == entryId }
    }

    override suspend fun getFilteredEntries(filter: FilterType): List<WorkEntry> {
        return when (filter) {
            FilterType.ALL -> _entries.value
            FilterType.TODAY -> {
                val today = LocalDate.now()
                _entries.value.filter { it.startTime.toLocalDate() == today }
            }
            FilterType.THIS_WEEK -> {
                val weekStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                val weekEnd = weekStart.plusDays(6)
                _entries.value.filter { entry ->
                    entry.startTime.toLocalDate() >= weekStart &&
                    entry.endTime.toLocalDate() <= weekEnd
                }
            }
            FilterType.THIS_MONTH -> {
                val monthStart = LocalDate.now().withDayOfMonth(1)
                val monthEnd = monthStart.plusMonths(1).minusDays(1)
                _entries.value.filter { entry ->
                    entry.startTime.toLocalDate() >= monthStart &&
                    entry.endTime.toLocalDate() <= monthEnd
                }
            }
            else -> _entries.value // Handle any future FilterType values
        }
    }
} 