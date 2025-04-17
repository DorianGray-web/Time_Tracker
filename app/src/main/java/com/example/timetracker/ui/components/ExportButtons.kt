package com.example.timetracker.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ExportButtons(
    onExportPdf: (Context) -> Uri?,
    onExportCsv: (Context) -> Uri?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val uri = onExportPdf(context)
                    if (uri != null) {
                        shareFile(context, uri, "application/pdf")
                        scope.launch {
                            snackbarHostState.showSnackbar("PDF exported successfully.")
                        }
                        Log.d("Export", "PDF saved at: $uri")
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Failed to export PDF.")
                        }
                        Log.e("Export", "PDF generation failed")
                    }
                }
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export PDF")
            }

            Button(
                onClick = {
                    val uri = onExportCsv(context)
                    if (uri != null) {
                        shareFile(context, uri, "text/csv")
                        scope.launch {
                            snackbarHostState.showSnackbar("CSV exported successfully.")
                        }
                        Log.d("Export", "CSV saved at: $uri")
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Failed to export CSV.")
                        }
                        Log.e("Export", "CSV generation failed")
                    }
                }
            ) {
                Icon(Icons.Default.TableChart, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export CSV")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SnackbarHost(hostState = snackbarHostState)
    }
}

private fun shareFile(context: Context, uri: Uri, mimeType: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
} 