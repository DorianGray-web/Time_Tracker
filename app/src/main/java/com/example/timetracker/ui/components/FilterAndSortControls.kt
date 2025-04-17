package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetracker.viewmodel.SortOption
import java.time.LocalDate

@Composable
fun FilterAndSortControls(
    currentSort: SortOption,
    onSortChange: (SortOption) -> Unit,
    dateRange: Pair<LocalDate?, LocalDate?>,
    onDateRangeChange: (LocalDate?, LocalDate?) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        // Sort Control
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sort by:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = showSortMenu,
                onExpandedChange = { showSortMenu = it }
            ) {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.menuAnchor()
                ) {
                    Text(currentSort.label)
                }
                
                ExposedDropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                onSortChange(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Range Control
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "From: ${dateRange.first?.toString() ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "To: ${dateRange.second?.toString() ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Button(
                onClick = { showDatePicker = true }
            ) {
                Text("Pick Date Range")
            }
        }

        // Date Range Picker Dialog
        if (showDatePicker) {
            DateRangePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateRangeSelected = { start, end ->
                    onDateRangeChange(start, end)
                    showDatePicker = false
                }
            )
        }
    }
} 