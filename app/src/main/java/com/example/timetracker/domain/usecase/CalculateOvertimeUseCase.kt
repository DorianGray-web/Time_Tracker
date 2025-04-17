package com.example.timetracker.domain.usecase

import com.example.timetracker.domain.model.WorkEntry
import java.time.Duration

class CalculateOvertimeUseCase(
    private val weeklyHours: Double,
    private val overtimeMultiplier: Double
) {
    fun execute(entries: List<WorkEntry>): Double {
        val totalWorked = entries.sumOf {
            Duration.between(it.startTime, it.endTime).toMinutes()
        } / 60.0
        val overtime = (totalWorked - weeklyHours).coerceAtLeast(0.0)
        return overtime * overtimeMultiplier
    }
} 