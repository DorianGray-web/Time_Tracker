package com.example.timetracker.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timetracker.viewmodel.WorkViewModel

@Composable
fun ExportPdfButton(
    viewModel: WorkViewModel = viewModel(),
    onExportComplete: (Uri?) -> Unit = { uri ->
        if (uri != null) {
            Log.d("Export", "PDF saved at: $uri")
        } else {
            Log.e("Export", "PDF generation failed")
        }
    }
) {
    var showDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
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
            Text("Export to PDF")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Export to PDF") },
            text = { Text("This will create a PDF report with all your work entries. Continue?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.exportToPdf { uri ->
                            if (uri != null) {
                                // Share the PDF
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
                                
                                snackbarMessage = "PDF exported successfully"
                            } else {
                                snackbarMessage = "Failed to export PDF"
                            }
                            showSnackbar = true
                            showDialog = false
                            onExportComplete(uri)
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

    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            showSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(snackbarMessage)
        }
    }
} 