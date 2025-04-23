package com.example.timetracker.data.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration

data class WorkEntry(
    val id: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val commentEn: String,
    val commentNl: String,
    val photoPath: String? = null
) {
    fun getDuration(): Long {
        return Duration.between(startTime, endTime).toHours()
    }
} 