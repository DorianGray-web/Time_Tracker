@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetracker.R
import com.example.timetracker.ui.components.EntryForm
import com.example.timetracker.viewmodel.WorkViewModel

@Composable
fun EditEntryScreen(
    entryId: Int,
    onBack: () -> Unit,
    viewModel: WorkViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState
    val message by viewModel.snackbarMessage.collectAsState(null)

    // Show Snackbar when message is emitted
    LaunchedEffect(message) {
        message?.let { text ->
            snackbarHostState.showSnackbar(text)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_entry_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { 
                            contentDescription = stringResource(R.string.cd_back_button)
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            EntryForm(
                entryId = entryId,
                onSubmit = { formData ->
                    viewModel.updateEntry(entryId, formData)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
} 