package com.example.timetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.timetracker.database.DateTimeConverters
import java.time.LocalDateTime

@Entity(tableName = "time_entries")
@TypeConverters(DateTimeConverters::class)
data class TimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val description: String = "",
    val isRunning: Boolean = false
) 