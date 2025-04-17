package com.example.timetracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.R
import com.example.timetracker.viewmodel.ExportFormat
import com.example.timetracker.viewmodel.MainViewModel

@Composable
fun ExportControls(viewModel: MainViewModel = viewModel()) {
    val exportFormat by viewModel.exportFormat.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Format selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.menuAnchor()
            ) {
                Text(exportFormat.name)
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ExportFormat.values().forEach { format ->
                    DropdownMenuItem(
                        text = { Text(format.name) },
                        onClick = {
                            viewModel.setExportFormat(format)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Export button
        Button(
            onClick = { viewModel.exportEntries() },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.export)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.export))
        }
    }
} 