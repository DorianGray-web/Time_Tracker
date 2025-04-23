package com.example.timetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "time_entries")
data class TimeEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var startTime: String,
    var endTime: String? = null,
    var description: String = "",
    var isRunning: Boolean = false
) 