package com.example.timetracker.presentation.backup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.R
import com.example.timetracker.presentation.components.LoadingIndicator
import com.example.timetracker.presentation.components.SnackbarHost
import com.example.timetracker.presentation.theme.spacing

@Composable
fun BackupScreen(
    viewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showRestoreDialog by remember { mutableStateOf(false) }
    
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.restoreFromBackup(it) }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.restoreFromBackup(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = it) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.backup_restore),
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = { viewModel.createBackup() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.create_backup))
            }

            Button(
                onClick = { showRestoreDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.restore_backup))
            }

            when (uiState) {
                is BackupUiState.Loading -> LoadingIndicator()
                is BackupUiState.Success -> {
                    val filePath = (uiState as BackupUiState.Success).filePath
                    Text(
                        text = stringResource(R.string.backup_created, filePath),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is BackupUiState.Error -> {
                    val message = (uiState as BackupUiState.Error).message
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is BackupUiState.RestoreSuccess -> {
                    Text(
                        text = stringResource(R.string.restore_success),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {}
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text(stringResource(R.string.restore_backup)) },
            text = { Text(stringResource(R.string.restore_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        restoreLauncher.launch(arrayOf("application/json"))
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
} 