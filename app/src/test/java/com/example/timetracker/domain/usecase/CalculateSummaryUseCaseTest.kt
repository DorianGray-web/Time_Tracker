package com.example.timetracker.domain.usecase

import com.example.timetracker.domain.model.WorkEntry
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class CalculateSummaryUseCaseTest {
    private val useCase = CalculateSummaryUseCase(
        hourlyRate = 15.0,
        weeklyHours = 40.0,
        overtimeMultiplier = 1.5
    )

    @Test
    fun `test regular hours calculation`() {
        val entries = listOf(
            WorkEntry(
                startTime = LocalDateTime.of(2024, 1, 1, 9, 0),
                endTime = LocalDateTime.of(2024, 1, 1, 17, 0),
                description = "Regular work"
            )
        )

        val summary = useCase.execute(entries)
        
        assertEquals(8.0, summary.totalHours)
        assertEquals(0.0, summary.overtimeHours)
        assertEquals(120.0, summary.totalEarnings) // 8 hours * $15
    }

    @Test
    fun `test overtime calculation`() {
        val entries = listOf(
            WorkEntry(
                startTime = LocalDateTime.of(2024, 1, 1, 9, 0),
                endTime = LocalDateTime.of(2024, 1, 1, 19, 0),
                description = "Overtime work"
            )
        )

        val summary = useCase.execute(entries)
        
        assertEquals(10.0, summary.totalHours)
        assertEquals(2.0, summary.overtimeHours)
        assertEquals(165.0, summary.totalEarnings) // (8 * $15) + (2 * $15 * 1.5)
    }

    @Test
    fun `test materials cost calculation`() {
        val entries = listOf(
            WorkEntry(
                startTime = LocalDateTime.of(2024, 1, 1, 9, 0),
                endTime = LocalDateTime.of(2024, 1, 1, 17, 0),
                description = "Work with materials",
                materialsCost = 50.0
            )
        )

        val summary = useCase.execute(entries)
        
        assertEquals(8.0, summary.totalHours)
        assertEquals(0.0, summary.overtimeHours)
        assertEquals(170.0, summary.totalEarnings) // (8 * $15) + $50 materials
    }
} 