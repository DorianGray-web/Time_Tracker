package com.example.timetracker.model

import java.time.LocalDate

data class FilterOptions(
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val onlyWithPhoto: Boolean = false,
    val commentContains: String? = null
) 