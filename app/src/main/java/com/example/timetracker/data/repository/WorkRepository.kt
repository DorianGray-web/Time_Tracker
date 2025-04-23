package com.example.timetracker.data.repository

import com.example.timetracker.data.local.dao.WorkEntryDao
import com.example.timetracker.data.local.entity.toWorkEntry
import com.example.timetracker.data.local.entity.toWorkEntryEntity
import com.example.timetracker.domain.model.FilterType
import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.repository.WorkEntryRepository
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WorkRepository @Inject constructor(
    private val workEntryDao: WorkEntryDao
) : WorkEntryRepository {
    override fun getAllEntries(): Flow<List<WorkEntry>> = 
        workEntryDao.getAllEntries().map { entries ->
            entries.map { it.toWorkEntry() }
        }

    override suspend fun getEntriesForWeek(weekStart: LocalDate): List<WorkEntry> {
        val weekEnd = weekStart.plusDays(6)
        return workEntryDao.getEntriesForDateRange(weekStart.toEpochDay(), weekEnd.toEpochDay())
            .map { it.toWorkEntry() }
    }

    override suspend fun addEntry(entry: WorkEntry) {
        workEntryDao.insert(entry.toWorkEntryEntity())
    }

    override suspend fun updateEntry(entry: WorkEntry) {
        workEntryDao.update(entry.toWorkEntryEntity())
    }

    override suspend fun deleteEntry(entryId: Long) {
        workEntryDao.delete(entryId)
    }

    override suspend fun getEntry(entryId: Long): WorkEntry? {
        return workEntryDao.getEntry(entryId)?.toWorkEntry()
    }

    override suspend fun getFilteredEntries(filter: FilterType): List<WorkEntry> {
        return when (filter) {
            FilterType.ALL -> workEntryDao.getAllEntriesList().map { it.toWorkEntry() }
            FilterType.TODAY -> {
                val today = LocalDate.now()
                workEntryDao.getEntriesForDate(today.toEpochDay()).map { it.toWorkEntry() }
            }
            FilterType.THIS_WEEK -> {
                val weekStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
                val weekEnd = weekStart.plusDays(6)
                workEntryDao.getEntriesForDateRange(weekStart.toEpochDay(), weekEnd.toEpochDay()).map { it.toWorkEntry() }
            }
            FilterType.THIS_MONTH -> {
                val monthStart = LocalDate.now().withDayOfMonth(1)
                val monthEnd = monthStart.plusMonths(1).minusDays(1)
                workEntryDao.getEntriesForDateRange(monthStart.toEpochDay(), monthEnd.toEpochDay()).map { it.toWorkEntry() }
            }
            else -> emptyList()
        }
    }
} 