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
import com.example.timetracker.domain.model.SortField
import com.example.timetracker.domain.model.SortOrder
import com.example.timetracker.domain.model.SortOptions
import com.example.timetracker.viewmodel.MainViewModel

@Composable
fun SortControls(viewModel: MainViewModel = hiltViewModel()) {
    var showSortDialog by remember { mutableStateOf(false) }
    val sortOptions by viewModel.sortOptions.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.sort_by),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = { showSortDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = stringResource(R.string.sort_entries)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (sortOptions.field) {
                    SortField.START_TIME -> stringResource(R.string.start_time)
                    SortField.DURATION -> stringResource(R.string.duration)
                    SortField.COST -> stringResource(R.string.cost)
                    SortField.DATE -> stringResource(R.string.date)
                }
            )
            Icon(
                imageVector = if (sortOptions.order == SortOrder.ASCENDING) 
                    Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = stringResource(
                    if (sortOptions.order == SortOrder.ASCENDING) 
                        R.string.ascending else R.string.descending
                )
            )
        }
    }

    if (showSortDialog) {
        SortDialog(
            currentSortOptions = sortOptions,
            onDismiss = { showSortDialog = false },
            onSortOptionsSelected = { newOptions ->
                viewModel.updateSortOptions(newOptions)
                showSortDialog = false
            }
        )
    }
}

@Composable
private fun SortDialog(
    currentSortOptions: SortOptions,
    onDismiss: () -> Unit,
    onSortOptionsSelected: (SortOptions) -> Unit
) {
    var selectedField by remember { mutableStateOf(currentSortOptions.field) }
    var selectedOrder by remember { mutableStateOf(currentSortOptions.order) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sort_entries)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.sort_by),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SortField.values().forEach { field ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = field == selectedField,
                            onClick = { selectedField = field }
                        )
                        Text(
                            text = when (field) {
                                SortField.START_TIME -> stringResource(R.string.start_time)
                                SortField.DURATION -> stringResource(R.string.duration)
                                SortField.COST -> stringResource(R.string.cost)
                                SortField.DATE -> stringResource(R.string.date)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.order),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SortOrder.values().forEach { order ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = order == selectedOrder,
                            onClick = { selectedOrder = order }
                        )
                        Text(
                            text = when (order) {
                                SortOrder.ASCENDING -> stringResource(R.string.ascending)
                                SortOrder.DESCENDING -> stringResource(R.string.descending)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSortOptionsSelected(
                        SortOptions(
                            field = selectedField,
                            order = selectedOrder
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
} 