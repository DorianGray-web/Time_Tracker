package com.example.timetracker.data.local.dao

import androidx.room.*
import com.example.timetracker.data.local.entity.WorkEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkEntryDao {
    @Query("SELECT * FROM work_entries")
    fun getAllEntries(): Flow<List<WorkEntryEntity>>

    @Query("SELECT * FROM work_entries WHERE id = :entryId")
    suspend fun getEntry(entryId: Long): WorkEntryEntity?

    @Query("SELECT * FROM work_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getEntriesForDateRange(startDate: Long, endDate: Long): List<WorkEntryEntity>

    @Query("SELECT * FROM work_entries WHERE date = :date")
    suspend fun getEntriesForDate(date: Long): List<WorkEntryEntity>

    @Query("SELECT * FROM work_entries")
    suspend fun getAllEntriesList(): List<WorkEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workEntryEntity: WorkEntryEntity)

    @Update
    suspend fun update(workEntryEntity: WorkEntryEntity)

    @Query("DELETE FROM work_entries WHERE id = :entryId")
    suspend fun delete(entryId: Long)
} 