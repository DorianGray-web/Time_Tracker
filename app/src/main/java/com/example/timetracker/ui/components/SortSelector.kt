package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.model.SortType
import com.example.timetracker.viewmodel.WorkViewModel

@Composable
fun SortSelector(viewModel: WorkViewModel = viewModel()) {
    val selectedSortType by viewModel.sortType.collectAsState()
    val sortOptions = mapOf(
        SortType.DATE_ASCENDING to "Oldest First",
        SortType.DATE_DESCENDING to "Newest First",
        SortType.WEEKLY to "This Week"
    )

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text("Sort By", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = sortOptions[selectedSortType] ?: "Select Sort Type",
                onValueChange = {},
                label = { Text("Sort Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sortOptions.forEach { (type, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.updateSortType(type)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
} 