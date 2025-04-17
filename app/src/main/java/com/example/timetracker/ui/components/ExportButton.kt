package com.example.timetracker.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.R
import com.example.timetracker.viewmodel.WorkViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExportButton(
    viewModel: WorkViewModel = viewModel(),
    onExportComplete: (Uri?) -> Unit = { uri ->
        if (uri != null) {
            Log.d("Export", "CSV saved at: $uri")
        } else {
            Log.e("Export", "Failed to export CSV")
        }
    }
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export to CSV")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Export to CSV") },
            text = { Text("This will create a CSV file with all your work entries. Continue?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.exportToCsv { uri ->
                            onExportComplete(uri)
                            showDialog = false
                        }
                    }
                ) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 