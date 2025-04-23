package com.example.timetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.R
import com.example.timetracker.data.model.WorkEntry
import com.example.timetracker.ui.components.*
import com.example.timetracker.viewmodel.SortOption
import com.example.timetracker.viewmodel.WorkViewModel
import java.time.LocalDate
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.timetracker.ui.util.WindowSizeClass
import com.example.timetracker.ui.util.rememberWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WorkViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    val windowSizeClass = rememberWindowSizeClass()
    val isTablet = windowSizeClass != WindowSizeClass.COMPACT
    
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    val workEntries by viewModel.workEntries.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isTablet) {
            // Tablet layout with master-detail
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Master panel (list)
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                ) {
                    Column {
                        FilterControls(
                            filterOptions = filterOptions,
                            updateFilterOptions = viewModel::updateFilterOptions
                        )
                        EntriesList(
                            entries = workEntries,
                            selectedEntryId = selectedEntry?.id,
                            onEntryClick = { viewModel.selectEntry(it.id) },
                            onAddClick = { viewModel.createNewEntry() }
                        )
                    }
                }

                // Divider
                VerticalDivider()

                // Detail panel
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                ) {
                    selectedEntry?.let { entry ->
                        EntryDetails(
                            entry = entry,
                            onEdit = { viewModel.editEntry(it) },
                            onDelete = { 
                                viewModel.deleteEntry(it)
                                viewModel.selectEntry(null)
                            }
                        )
                    } ?: EmptyDetailsPlaceholder()
                }
            }
        } else {
            // Phone layout with single panel
            EntriesList(
                entries = workEntries,
                selectedEntryId = null,
                onEntryClick = { onNavigateToDetails(it.id) },
                onAddClick = { viewModel.createNewEntry() }
            )
        }
    }
}

@Composable
private fun EntriesList(
    entries: List<WorkEntry>,
    selectedEntryId: Int?,
    onEntryClick: (WorkEntry) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.time_entries_title),
                style = MaterialTheme.typography.headlineMedium
            )
            
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(R.string.cd_add_entry)
                    role = Role.Button
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(entries) { entry ->
                EntryListItem(
                    entry = entry,
                    isSelected = entry.id == selectedEntryId,
                    onClick = { onEntryClick(entry) }
                )
            }
        }
    }
}

@Composable
private fun EntryListItem(
    entry: WorkEntry,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = stringResource(
                    R.string.cd_time_entry_item,
                    entry.date.toString(),
                    entry.startTime,
                    entry.endTime
                )
                role = Role.Button
                selected = isSelected
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = entry.date.toString(),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${entry.startTime} - ${entry.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (entry.hasPhoto) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = stringResource(R.string.cd_entry_has_photo),
                    modifier = Modifier.semantics { role = Role.Image }
                )
            }
        }
    }
}

@Composable
private fun EntryDetails(
    entry: WorkEntry,
    onEdit: (WorkEntry) -> Unit,
    onDelete: (WorkEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.entry_details_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DetailRow(
                    label = stringResource(R.string.label_date),
                    value = entry.date.toString()
                )
                DetailRow(
                    label = stringResource(R.string.start_time),
                    value = entry.startTime
                )
                DetailRow(
                    label = stringResource(R.string.end_time),
                    value = entry.endTime
                )
                DetailRow(
                    label = stringResource(R.string.duration_label),
                    value = entry.getDuration().toString()
                )
                
                if (entry.hasPhoto) {
                    AsyncImage(
                        model = entry.photoUri,
                        contentDescription = stringResource(R.string.cd_entry_photo),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .semantics { role = Role.Image }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onEdit(entry) },
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(R.string.cd_edit_entry)
                            role = Role.Button
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.edit_entry_button))
                    }
                    
                    TextButton(
                        onClick = { onDelete(entry) },
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(R.string.cd_delete_entry)
                            role = Role.Button
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.delete_entry_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyDetailsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.select_entry_prompt),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FilterAndSortControls(
    currentSort: SortOption,
    currentDateRange: Pair<LocalDate?, LocalDate?>,
    onSortChange: (SortOption) -> Unit,
    onDateRangeChange: (Pair<LocalDate?, LocalDate?>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SortDropdown(
                selectedSort = currentSort,
                onSortChange = onSortChange
            )
            DateRangePicker(
                currentRange = currentDateRange,
                onRangeChange = onDateRangeChange
            )
        }
    }
}

@Composable
fun SortDropdown(
    selectedSort: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.menuAnchor()
        ) {
            Text(selectedSort.label)
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DateRangePicker(
    currentRange: Pair<LocalDate?, LocalDate?>,
    onRangeChange: (Pair<LocalDate?, LocalDate?>) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "From: ${currentRange.first?.toString() ?: "-"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "To: ${currentRange.second?.toString() ?: "-"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = { showDatePicker = true }
        ) {
            Text("Pick Date Range")
        }
    }

    if (showDatePicker) {
        DateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateRangeSelected = { start, end ->
                onRangeChange(Pair(start, end))
                showDatePicker = false
            }
        )
    }
}

@Composable
fun WorkEntryCard(
    entry: WorkEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = entry.date.toString(),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${entry.startTime} - ${entry.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Duration: ${entry.getDuration()} hours",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Comment (EN): ${entry.commentEn}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Comment (NL): ${entry.commentNl}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
} 