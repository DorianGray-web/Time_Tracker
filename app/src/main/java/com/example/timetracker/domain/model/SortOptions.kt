package com.example.timetracker.domain.model

enum class SortField {
    START_TIME,
    DURATION,
    COST,
    DATE
}

enum class SortOrder {
    ASCENDING,
    DESCENDING
}

data class SortOptions(
    val field: SortField = SortField.DATE,
    val order: SortOrder = SortOrder.DESCENDING
) 