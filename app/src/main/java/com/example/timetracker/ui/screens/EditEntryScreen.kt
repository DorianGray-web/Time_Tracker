package com.example.timetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.timetracker.R
import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEntryScreen(
    navController: NavController,
    entryId: Long?,
    viewModel: MainViewModel = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var entry by remember { mutableStateOf<WorkEntry?>(null) }

    LaunchedEffect(entryId) {
        entryId?.let { id ->
            viewModel.getEntry(id)?.let { workEntry ->
                entry = workEntry
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_entry_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigation_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.semantics { contentDescription = stringResource(R.string.cd_delete_entry_button) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_entry_button)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .semantics { contentDescription = stringResource(R.string.cd_edit_entry_screen) }
        ) {
            entry?.let { workEntry ->
                EntryForm(
                    initialEntry = workEntry,
                    onSave = { updatedEntry ->
                        viewModel.updateEntry(updatedEntry)
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        entryId?.let { id ->
                            viewModel.deleteEntry(id)
                            navController.navigateUp()
                        }
                    }
                ) {
                    Text(stringResource(R.string.button_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.button_cancel))
                }
            },
            modifier = Modifier.semantics { contentDescription = stringResource(R.string.cd_confirm_delete_dialog) }
        )
    }
} 