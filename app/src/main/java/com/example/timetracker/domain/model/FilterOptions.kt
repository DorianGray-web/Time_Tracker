package com.example.timetracker.domain.model

import java.time.LocalDate

data class FilterOptions(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val weekNumber: Int? = null,
    val projectName: String? = null,
    val clientName: String? = null,
    val showOvertime: Boolean = false,
    val showOnlyWithPhotos: Boolean = false
) 