@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.R
import com.example.timetracker.domain.model.FilterOptions
import com.example.timetracker.domain.model.FilterType
import com.example.timetracker.viewmodel.MainViewModel
import java.time.LocalDate

/**
 * @param filterOptions — текущее состояние фильтров
 * @param updateFilterOptions — лямбда для изменения фильтров
 */
@Composable
fun FilterControls(
    filterOptions: FilterOptions,
    updateFilterOptions: (FilterOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showWeekPicker by remember { mutableStateOf(false) }
    var showProjectFilter by remember { mutableStateOf(false) }
    var showClientFilter by remember { mutableStateOf(false) }
    
    val viewModel: MainViewModel = hiltViewModel()
    val filterType by viewModel.filterType.collectAsState()

    Column(modifier) {
        Text(
            text = stringResource(R.string.filter),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == FilterType.ALL,
                onClick = { viewModel.updateFilterType(FilterType.ALL) },
                label = { Text(stringResource(R.string.all_entries)) },
                modifier = Modifier.semantics { contentDescription = stringResource(R.string.cd_filter_all) }
            )
            FilterChip(
                selected = filterType == FilterType.TODAY,
                onClick = { viewModel.updateFilterType(FilterType.TODAY) },
                label = { Text(stringResource(R.string.today)) },
                modifier = Modifier.semantics { contentDescription = stringResource(R.string.cd_filter_today) }
            )
            FilterChip(
                selected = filterType == FilterType.THIS_WEEK,
                onClick = { viewModel.updateFilterType(FilterType.THIS_WEEK) },
                label = { Text(stringResource(R.string.this_week)) },
                modifier = Modifier.semantics { contentDescription = stringResource(R.string.cd_filter_week) }
            )
            FilterChip(
                selected = filterType == FilterType.THIS_MONTH,
                onClick = { viewModel.updateFilterType(FilterType.THIS_MONTH) },
                label = { Text(stringResource(R.string.this_month)) },
                modifier = Modifier.semantics { contentDescription = stringResource(R.string.cd_filter_month) }
            )
        }

        // Date Range Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Date Range:",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { showDateRangePicker = true },
                modifier = Modifier
                    .semantics { contentDescription = stringResource(R.string.select_date_range) }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.select_date_range)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${filterOptions.startDate ?: "Start"} - ${filterOptions.endDate ?: "End"}"
                )
            }
        }

        // Week Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Week:",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { showWeekPicker = true },
                modifier = Modifier
                    .semantics { contentDescription = stringResource(R.string.select_week) }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.select_week)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = filterOptions.weekNumber?.toString() ?: "All"
                )
            }
        }

        // Project Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Project:",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { showProjectFilter = true },
                modifier = Modifier
                    .semantics { contentDescription = stringResource(R.string.filter_by_project) }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = stringResource(R.string.filter_by_project)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = filterOptions.projectName ?: "All"
                )
            }
        }

        // Client Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Client:",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { showClientFilter = true },
                modifier = Modifier
                    .semantics { contentDescription = stringResource(R.string.filter_by_client) }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.filter_by_client)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = filterOptions.clientName ?: "All"
                )
            }
        }

        // Toggle Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = filterOptions.showOvertime,
                onClick = {
                    updateFilterOptions(
                        filterOptions.copy(showOvertime = !filterOptions.showOvertime)
                    )
                },
                label = { Text(stringResource(R.string.show_overtime_switch)) }
            )
            FilterChip(
                selected = filterOptions.showOnlyWithPhotos,
                onClick = {
                    updateFilterOptions(
                        filterOptions.copy(showOnlyWithPhotos = !filterOptions.showOnlyWithPhotos)
                    )
                },
                label = { Text(stringResource(R.string.with_photos_switch)) }
            )
        }

        // Date Range Picker Dialog
        if (showDateRangePicker) {
            DateRangePickerDialog(
                onDismiss = { showDateRangePicker = false },
                onDateRangeSelected = { start, end ->
                    updateFilterOptions(
                        filterOptions.copy(
                            startDate = start,
                            endDate = end
                        )
                    )
                    showDateRangePicker = false
                }
            )
        }

        // Week Picker Dialog
        if (showWeekPicker) {
            WeekPickerDialog(
                initialWeek = filterOptions.weekNumber,
                onWeekSelected = { week ->
                    updateFilterOptions(
                        filterOptions.copy(weekNumber = week)
                    )
                    showWeekPicker = false
                }
            )
        }

        // Project Filter Dialog
        if (showProjectFilter) {
            TextFilterDialog(
                title = stringResource(R.string.filter_by_project),
                onDismiss = { showProjectFilter = false },
                onTextSelected = { project ->
                    updateFilterOptions(
                        filterOptions.copy(projectName = project)
                    )
                    showProjectFilter = false
                }
            )
        }

        // Client Filter Dialog
        if (showClientFilter) {
            TextFilterDialog(
                title = stringResource(R.string.filter_by_client),
                onDismiss = { showClientFilter = false },
                onTextSelected = { client ->
                    updateFilterOptions(
                        filterOptions.copy(clientName = client)
                    )
                    showClientFilter = false
                }
            )
        }
    }
}

/** Снова, заглушки — нужно заменить на свой диалог */
@Composable
private fun WeekPickerDialog(
    initialWeek: Int?,
    onWeekSelected: (Int?) -> Unit
) {
    // здесь ваш код диалога. Пока пустая заглушка:
}

@Composable
private fun TextFilterDialog(
    title: String,
    onDismiss: () -> Unit,
    onTextSelected: (String) -> Unit
) {
    // здесь ваш код диалога. Пока пустая заглушка:
} 