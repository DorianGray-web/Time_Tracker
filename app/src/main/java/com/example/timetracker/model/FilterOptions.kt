package com.example.timetracker.model

import java.time.LocalDate

/**
 * Data class representing filter options for work entries
 */
data class FilterOptions(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val showOvertime: Boolean = false,
    val showOnlyWithPhotos: Boolean = false
) 