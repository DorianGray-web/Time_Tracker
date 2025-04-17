package com.example.timetracker.domain.model

import android.net.Uri
import java.time.LocalDateTime
import java.time.Duration

data class WorkEntry(
    val id: Long = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val description: String,
    val materialsCost: Double = 0.0,
    val commentEn: String? = null,
    val commentNl: String? = null,
    val photos: List<Uri> = emptyList()
) {
    fun getDuration(): Duration = Duration.between(startTime, endTime)
    
    fun getDurationHours(): Double = getDuration().toMinutes() / 60.0
    
    fun isOverlapping(other: WorkEntry): Boolean {
        return !(endTime.isBefore(other.startTime) || startTime.isAfter(other.endTime))
    }
} 