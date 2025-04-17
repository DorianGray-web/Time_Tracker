package com.example.timetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "work_entries")
data class WorkEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val commentEn: String,
    val commentNl: String,
    val photoPath: String? = null
) {
    fun getDuration(): Double {
        val start = startTime.toSecondOfDay().toDouble()
        val end = endTime.toSecondOfDay().toDouble()
        return (end - start) / 3600.0 // Convert seconds to hours
    }
} 