package com.example.timetracker.domain.usecase

import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.model.WorkSummary
import java.time.Duration

class CalculateSummaryUseCase(
    private val hourlyRate: Double,
    private val weeklyHours: Double,
    private val overtimeMultiplier: Double
) {
    fun execute(entries: List<WorkEntry>): WorkSummary {
        val totalMinutes = entries.sumOf {
            Duration.between(it.startTime, it.endTime).toMinutes()
        }
        val totalHours = totalMinutes / 60.0
        val overtimeHours = (totalHours - weeklyHours).coerceAtLeast(0.0)

        val baseHours = totalHours - overtimeHours
        val earnings = (baseHours * hourlyRate) +
                (overtimeHours * hourlyRate * overtimeMultiplier) +
                entries.sumOf { it.materialsCost }

        return WorkSummary(totalHours, overtimeHours, earnings)
    }
} 