package com.example.timetracker.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetracker.model.FilterOptions

@Composable
fun FilterOptionsDialog(
    onDismiss: () -> Unit,
    onConfirm: (FilterOptions) -> Unit,
    initialOptions: FilterOptions = FilterOptions()
) {
    var showOvertime by remember { mutableStateOf(initialOptions.showOvertime) }
    var showOnlyWithPhotos by remember { mutableStateOf(initialOptions.showOnlyWithPhotos) }
    var showDateRangeDialog by remember { mutableStateOf(false) }

    if (showDateRangeDialog) {
        DateRangeDialog(
            onDismiss = { showDateRangeDialog = false },
            onConfirm = { startDate, endDate ->
                onConfirm(FilterOptions(
                    startDate = startDate,
                    endDate = endDate,
                    showOvertime = showOvertime,
                    showOnlyWithPhotos = showOnlyWithPhotos
                ))
            },
            initialStartDate = initialOptions.startDate,
            initialEndDate = initialOptions.endDate
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Options") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showDateRangeDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Date Range")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Show Overtime")
                    Switch(
                        checked = showOvertime,
                        onCheckedChange = { showOvertime = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Show Only With Photos")
                    Switch(
                        checked = showOnlyWithPhotos,
                        onCheckedChange = { showOnlyWithPhotos = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(FilterOptions(
                        startDate = initialOptions.startDate,
                        endDate = initialOptions.endDate,
                        showOvertime = showOvertime,
                        showOnlyWithPhotos = showOnlyWithPhotos
                    ))
                    onDismiss()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 