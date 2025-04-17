package com.example.timetracker.data

import androidx.room.*
import com.example.timetracker.model.WorkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkEntryDao {
    @Query("SELECT * FROM work_entries ORDER BY date DESC, startTime DESC")
    fun getAllWorkEntries(): Flow<List<WorkEntry>>

    @Query("SELECT * FROM work_entries WHERE date = :date")
    fun getWorkEntriesByDate(date: String): Flow<List<WorkEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WorkEntry)

    @Delete
    suspend fun delete(entry: WorkEntry)

    @Update
    suspend fun update(entry: WorkEntry)
} 